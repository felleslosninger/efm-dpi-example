package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentUtils;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.messagetypes.DokumentpakkefingeravtrykkHolder;
import no.digdir.dpi.client.domain.messagetypes.MaskinportentokenHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StandBusinessDocumentJsonFinalizer {

    private final CreateParcelFingerprint createParcelFingerprint;
    private final DpiMapper dpiMapper;
    private final JsonDigitalPostSchemaValidator jsonDigitalPostSchemaValidator;

    public Map<String, Object> getFinalizedStandardBusinessDocumentAsJson(StandardBusinessDocument standardBusinessDocument,
                                                                          CmsEncryptedAsice cmsEncryptedAsice,
                                                                          String maskinportenToken) {
        standardBusinessDocument.getBusinessMessage(DokumentpakkefingeravtrykkHolder.class)
                .ifPresent(p -> p.setDokumentpakkefingeravtrykk(createParcelFingerprint.createParcelFingerprint(cmsEncryptedAsice)));

        standardBusinessDocument.getBusinessMessage(MaskinportentokenHolder.class)
                .ifPresent(message -> message.setMaskinportentoken(maskinportenToken));

        Map<String, Object> json = dpiMapper.convertToJsonObject(standardBusinessDocument);
        jsonDigitalPostSchemaValidator.validate(json, StandardBusinessDocumentUtils.getType(standardBusinessDocument).orElse(null));
        return json;
    }
}
