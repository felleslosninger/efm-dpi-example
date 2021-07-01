package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Message;
import no.digdir.dpi.client.domain.MessageStatus;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.*;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DpiClientImpl implements DpiClient {

    private final CreateCmsEncryptedAsice createCmsEncryptedAsice;
    private final CreateMaskinportenToken createMaskinportenToken;
    private final CreateParcelFingerprint createParcelFingerprint;
    private final StandBusinessDocumentJsonFinalizer standBusinessDocumentJsonFinalizer;
    private final CreateJWT createJWT;
    private final CreateMultipart createMultipart;
    private final WebClient webClient;
    private final DpiClientErrorHandler dpiClientErrorHandler;
    private final InMemoryWithTempFileFallbackResourceFactory resourceFactory;

    @Override
    @SneakyThrows
    public void send(Shipment shipment) {
        try (CmsEncryptedAsice cmsEncryptedAsice = createCmsEncryptedAsice.createCmsEncryptedAsice(shipment)) {
            String maskinportenToken = createMaskinportenToken.getMaskinportenToken();

            Map<String, Object> finalizedSBD = standBusinessDocumentJsonFinalizer.getFinalizedStandardBusinessDocumentAsJson(
                    shipment.getStandardBusinessDocument(),
                    createParcelFingerprint.createParcelFingerprint(cmsEncryptedAsice),
                    maskinportenToken);

            String jwt = createJWT.createJWT(finalizedSBD);

            webClient.post()
                    .uri("/send")
                    .headers(h -> h.setBearerAuth(maskinportenToken))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(createMultipart.createMultipart(jwt, cmsEncryptedAsice)))
                    .retrieve()
                    .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                    .toBodilessEntity()
                    .block();
        } catch (DpiException e) {
            throw e;
        } catch (Exception e) {
            throw new DpiException("Sending failed!", e, Blame.CLIENT);
        }
    }

    @Override
    public Flux<MessageStatus> getMessageStatuses(UUID identifier) {
        return webClient.get()
                .uri("/statuses/{identifier}", identifier)
                .retrieve()
                .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                .bodyToFlux(MessageStatus.class);
    }

    @Override
    public Flux<Message> getMessages() {
        return webClient.get()
                .uri("/messages")
                .retrieve()
                .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                .bodyToFlux(Message.class);
    }

    @Override
    public CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException {
        InMemoryWithTempFileFallbackResource cms = resourceFactory.getResource("dpi-", ".asic.cms");
        Flux<DataBuffer> dataBuffer = webClient.get()
                .uri(downloadurl)
                .retrieve()
                .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                .bodyToFlux(DataBuffer.class);

        try (OutputStream outputStream = cms.getOutputStream()) {
            DataBufferUtils.write(dataBuffer, outputStream)
                    .share().blockLast();
        } catch (IOException e) {
            throw new DpiException(
                    String.format("Downloading CMS encrypted archive failed for URL: %s", downloadurl),
                    e,
                    Blame.CLIENT);
        }

        return new CmsEncryptedAsice(cms);
    }

    @Override
    public void markAsRead(UUID identifier) {
        webClient.post()
                .uri("/setmessageread/{identifier}", identifier)
                .retrieve()
                .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                .toBodilessEntity()
                .block();
    }
}