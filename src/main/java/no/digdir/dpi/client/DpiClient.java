package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import no.digdir.dpi.client.internal.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class DpiClient {

    private final CreateCmsEncryptedAsice createCmsEncryptedAsice;
    private final CreateMaskinportenToken createMaskinportenToken;
    private final CreateParcelFingerprint createParcelFingerprint;
    private final CreateJWT createJWT;
    private final CreateMultipart createMultipart;
    private final WebClient webClient;

    @SneakyThrows
    public void send(Shipment shipment) {
        CmsEncryptedAsice cmsEncryptedAsice = createCmsEncryptedAsice.createCmsEncryptedAsice(shipment);
        StandardBusinessDocument standardBusinessDocument = shipment.getStandardBusinessDocument();

        String maskinportenToken = createMaskinportenToken.getMaskinportenToken();

        standardBusinessDocument.getDigitalpost()
                .setDokumentpakkefingeravtrykk(createParcelFingerprint.createParcelFingerprint(cmsEncryptedAsice))
                .setMaskinportentoken(maskinportenToken);

        String jwt = createJWT.createJWT(standardBusinessDocument);

        webClient.post()
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", maskinportenToken))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipart.createMultipart(jwt, cmsEncryptedAsice)))
                .retrieve()
                .onStatus(HttpStatus::isError, e -> e.bodyToMono(String.class)
                        .flatMap(s -> Mono.error(new Exception("http status: " + e.statusCode() + ", body: " + s)))
                )
                .toBodilessEntity()
                .block();
    }
}
