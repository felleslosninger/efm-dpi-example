package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.messagetypes.BusinessMessage;
import no.digdir.dpi.client.domain.messagetypes.DokumentpakkefingeravtrykkHolder;
import no.digdir.dpi.client.domain.messagetypes.MaskinportentokenHolder;
import no.digdir.dpi.client.domain.sbd.Dokumentpakkefingeravtrykk;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
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
        BusinessMessage<? extends BusinessMessage<?>> message = standardBusinessDocument.getBusinessMessage();

        if (message instanceof DokumentpakkefingeravtrykkHolder) {
            ((DokumentpakkefingeravtrykkHolder) message).setDokumentpakkefingeravtrykk(dokumentpakkefingeravtrykk);
        }

        if (message instanceof MaskinportentokenHolder) {
            ((MaskinportentokenHolder) message).setMaskinportentoken(maskinportenToken);
        }

        Map<String, Object> json = dpiMapper.convertToJsonObject(standardBusinessDocument);
        jsonDigitalPostSchemaValidator.validate(json, standardBusinessDocument.getType());
        return json;
    }
}
