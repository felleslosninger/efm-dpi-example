package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.sbd.Dokumentpakkefingeravtrykk;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import no.digdir.dpi.client.internal.CreateDokumentpakke;
import no.digdir.dpi.client.internal.CreateJWT;
import no.digdir.dpi.client.internal.CreateMaskinportenToken;
import no.digdir.dpi.client.internal.CreateMultipart;
import no.digipost.api.representations.Dokumentpakke;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class DpiClient {

    private final CreateDokumentpakke createDokumentpakke;
    private final CreateMaskinportenToken createMaskinportenToken;
    private final CreateJWT createJWT;
    private final CreateMultipart createMultipart;
    private final WebClient webClient;

    @SneakyThrows
    public void send(Shipment shipment) {
        Dokumentpakke dokumentpakke = createDokumentpakke.createDokumentpakke(shipment);
        StandardBusinessDocument standardBusinessDocument = shipment.getStandardBusinessDocument();

        String maskinportenToken = createMaskinportenToken.getMaskinportenToken();

        standardBusinessDocument.getDigitalpost()
                .setDokumentpakkefingeravtrykk(getParcelFingerprint(dokumentpakke))
                .setMaskinportentoken(maskinportenToken);

        String jwt = createJWT.createJWT(standardBusinessDocument);

        webClient.post()
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", maskinportenToken))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipart.createMultipart(jwt, dokumentpakke)))
                .retrieve()
                .onStatus(HttpStatus::isError, e -> e.bodyToMono(String.class)
                        .flatMap(s -> Mono.error(new Exception("http status: " + e.statusCode() + ", body: " + s)))
                )
                .toBodilessEntity()
                .block();
    }

    private Dokumentpakkefingeravtrykk getParcelFingerprint(Dokumentpakke dokumentpakke) throws IOException {
        return new Dokumentpakkefingeravtrykk()
                .setDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256")
                .setDigestValue(Base64.getEncoder().encodeToString(dokumentpakke.getSHA256()));
    }

    private static class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }
    }
}
