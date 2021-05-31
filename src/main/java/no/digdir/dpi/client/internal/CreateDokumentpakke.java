package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.domain.ArchivedASiCE;
import no.digdir.dpi.client.internal.domain.CMSDocument;
import no.digipost.api.representations.Dokumentpakke;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateDokumentpakke {

    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;

    public Dokumentpakke createDokumentpakke(Shipment shipment) {
        log.info("Creating dokumentpakke");
        ArchivedASiCE archivedASiCE = createASiCE.createAsice(shipment);

        log.info("Creating CMS document");
        CMSDocument cms = createCMS.createCMS(archivedASiCE.getBytes(), shipment.getReceiverBusinessCertificate());
        return new Dokumentpakke(cms.getBytes());
    }
}
