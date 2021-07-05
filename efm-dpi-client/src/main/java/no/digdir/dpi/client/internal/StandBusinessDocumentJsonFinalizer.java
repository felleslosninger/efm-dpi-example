package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentUtils;
import no.digdir.dpi.client.domain.messagetypes.DokumentpakkefingeravtrykkHolder;
import no.digdir.dpi.client.domain.messagetypes.MaskinportentokenHolder;
import no.digdir.dpi.client.domain.sbd.Dokumentpakkefingeravtrykk;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StandBusinessDocumentJsonFinalizer {

    private final DpiMapper dpiMapper;
    private final JsonDigitalPostSchemaValidator jsonDigitalPostSchemaValidator;

    public Map<String, Object> getFinalizedStandardBusinessDocumentAsJson(StandardBusinessDocument standardBusinessDocument,
                                                                          Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk,
                                                                          String maskinportenToken) {
        Object message = standardBusinessDocument.getAny();

        if (message instanceof DokumentpakkefingeravtrykkHolder) {
            ((DokumentpakkefingeravtrykkHolder) message).setDokumentpakkefingeravtrykk(dokumentpakkefingeravtrykk);
        }

        if (message instanceof MaskinportentokenHolder) {
            ((MaskinportentokenHolder) message).setMaskinportentoken(maskinportenToken);
        }

        Map<String, Object> json = dpiMapper.convertToJsonObject(standardBusinessDocument);
        jsonDigitalPostSchemaValidator.validate(json, StandardBusinessDocumentUtils.getType(standardBusinessDocument).orElse(null));
        return json;
    }
}
