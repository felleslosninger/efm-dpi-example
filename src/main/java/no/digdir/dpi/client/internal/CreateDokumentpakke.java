package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.Shipment;
import no.digipost.api.representations.Dokumentpakke;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateDokumentpakke {

    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;

    public Dokumentpakke createDokumentpakke(Shipment shipment) {
        log.info("Creating dokumentpakke");

        ByteArrayOutputStream asice = new ByteArrayOutputStream();
        createASiCE.createAsice(shipment, asice);

        log.info("Creating CMS document");

        ByteArrayOutputStream cms = new ByteArrayOutputStream();

        createCMS.createCMS(new ByteArrayInputStream(asice.toByteArray()), cms, shipment.getReceiverBusinessCertificate());
        return new Dokumentpakke(cms.toByteArray());
    }
}
