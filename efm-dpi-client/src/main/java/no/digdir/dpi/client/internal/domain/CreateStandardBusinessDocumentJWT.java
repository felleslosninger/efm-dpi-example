package no.digdir.dpi.client.internal.domain;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.CreateJWT;
import no.digdir.dpi.client.internal.StandBusinessDocumentJsonFinalizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateStandardBusinessDocumentJWT {

    private final StandBusinessDocumentJsonFinalizer standBusinessDocumentJsonFinalizer;
    private final CreateJWT createJWT;

    public String createStandardBusinessDocumentJWT(Shipment shipment, CmsEncryptedAsice cmsEncryptedAsice, String maskinportenToken) {
        Map<String, Object> finalizedSBD = standBusinessDocumentJsonFinalizer.getFinalizedStandardBusinessDocumentAsJson(
                shipment.getStandardBusinessDocument(),
                cmsEncryptedAsice,
                maskinportenToken);

        return createJWT.createJWT(finalizedSBD);
    }
}