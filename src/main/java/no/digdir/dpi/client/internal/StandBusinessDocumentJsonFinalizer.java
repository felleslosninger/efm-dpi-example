package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.sbd.Dokumentpakkefingeravtrykk;
import no.digdir.dpi.client.domain.sbd.Message;
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
        Message<? extends Message<?>> message = standardBusinessDocument.getMessage();

        message.setDokumentpakkefingeravtrykk(dokumentpakkefingeravtrykk);
        message.setMaskinportentoken(maskinportenToken);

        Map<String, Object> json = dpiMapper.convertToJsonObject(standardBusinessDocument);
        jsonDigitalPostSchemaValidator.validate(json, standardBusinessDocument.getType());
        return json;
    }
}
