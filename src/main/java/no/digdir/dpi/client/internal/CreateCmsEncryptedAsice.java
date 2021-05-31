package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Shipment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCmsEncryptedAsice {

    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;

    public CmsEncryptedAsice createCmsEncryptedAsice(Shipment shipment) {
        log.info("Creating ASiC-E");

        ByteArrayOutputStream asice = new ByteArrayOutputStream();
        createASiCE.createAsice(shipment, asice);

        log.info("CMS encrypting ASiC-E");

        ByteArrayOutputStream cms = new ByteArrayOutputStream();

        createCMS.createCMS(new ByteArrayInputStream(asice.toByteArray()), cms, shipment.getReceiverBusinessCertificate());

        return new CmsEncryptedAsice(new ByteArrayResource(cms.toByteArray()));
    }
}
