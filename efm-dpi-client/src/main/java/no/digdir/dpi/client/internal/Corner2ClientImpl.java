package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.io.InMemoryWithTempFileFallbackResource;
import no.difi.move.common.io.InMemoryWithTempFileFallbackResourceFactory;
import no.digdir.dpi.client.Blame;
import no.digdir.dpi.client.DpiException;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Message;
import no.digdir.dpi.client.domain.MessageStatus;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
public class Corner2ClientImpl implements Corner2Client {

    private final WebClient webClient;
    private final DpiClientErrorHandler dpiClientErrorHandler;
    private final CreateMaskinportenToken createMaskinportenToken;
    private final CreateMultipart createMultipart;
    private final InMemoryWithTempFileFallbackResourceFactory resourceFactory;

    @Override
    public void sendMessage(String maskinportentoken, String jwt, CmsEncryptedAsice cmsEncryptedAsice) {
        webClient.post()
                .uri("/send")
                .headers(h -> h.setBearerAuth(maskinportentoken))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipart.createMultipart(jwt, cmsEncryptedAsice)))
                .retrieve()
                .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                .toBodilessEntity()
                .block();
    }

    @Override
    public Flux<MessageStatus> getMessageStatuses(UUID messageId) {
        return webClient.get()
                .uri("/statuses/{identifier}", messageId)
                .headers(h -> h.setBearerAuth(createMaskinportenToken.createMaskinportenTokenForReceiving()))
                .retrieve()
                .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                .bodyToFlux(MessageStatus.class);
    }

    @Override
    public Flux<Message> getMessages(String avsenderidentifikator) {
        return webClient.get()
                .uri(uriBuilder -> getAvsenderidentifikator(uriBuilder, avsenderidentifikator))
                .headers(h -> h.setBearerAuth(createMaskinportenToken.createMaskinportenTokenForReceiving()))
                .retrieve()
                .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                .bodyToFlux(Message.class);
    }

    private URI getAvsenderidentifikator(UriBuilder uriBuilder, String avsenderidentifikator) {
        uriBuilder.path("/messages");
        if (avsenderidentifikator != null) {
            uriBuilder.queryParam("avsenderidentifikator", avsenderidentifikator);
        }
        return uriBuilder.build();
    }

    @Override
    public CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException {
        InMemoryWithTempFileFallbackResource cms = resourceFactory.getResource("dpi-", ".asic.cms");
        Flux<DataBuffer> dataBuffer = webClient.get()
                .uri(downloadurl)
                .headers(h -> h.setBearerAuth(createMaskinportenToken.createMaskinportenTokenForReceiving()))
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
    public void markAsRead(UUID messageId) {
        webClient.post()
                .uri("/setmessageread/{identifier}", messageId)
                .headers(h -> h.setBearerAuth(createMaskinportenToken.createMaskinportenTokenForReceiving()))
                .retrieve()
                .onStatus(HttpStatus::isError, this.dpiClientErrorHandler)
                .toBodilessEntity()
                .block();
    }
}
