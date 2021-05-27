package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.SendOutput;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.sbd.ParcelFingerprint;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import no.digdir.dpi.client.internal.CreateDokumentpakke;
import no.digdir.dpi.client.internal.CreateJWT;
import no.digdir.dpi.client.internal.CreateMaskinportenToken;
import no.digdir.dpi.client.internal.CreateMultipart;
import no.digdir.dpi.client.internal.domain.Billable;
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
    public SendOutput send(Shipment shipment) {
        Billable<Dokumentpakke> billable = createDokumentpakke.createDokumentpakke(shipment);
        Dokumentpakke dokumentpakke = billable.getEntity();
        StandardBusinessDocument standardBusinessDocument = shipment.getStandardBusinessDocument();

        String maskinportenToken = createMaskinportenToken.getMaskinportenToken();

        standardBusinessDocument.getDigitalpost()
                .setParcelFingerprint(getParcelFingerprint(dokumentpakke))
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

        return new SendOutput(billable.getBillableBytes());
    }

    private ParcelFingerprint getParcelFingerprint(Dokumentpakke dokumentpakke) throws IOException {
        return new ParcelFingerprint()
                .setDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256")
                .setDigestValue(Base64.getEncoder().encodeToString(dokumentpakke.getSHA256()));
    }

    private static class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }
    }
}
