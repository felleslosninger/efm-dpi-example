package no.digdir.dpi.client.internal;

import com.nimbusds.jose.Payload;
import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnpackStandardBusinessDocument {

    private final JsonDigitalPostSchemaValidator jsonDigitalPostSchemaValidator;
    private final DpiMapper dpiMapper;

    public StandardBusinessDocument unpackStandardBusinessDocument(String payload) {
        StandardBusinessDocument standardBusinessDocument = dpiMapper.readStandardBusinessDocument(payload);
        String type = StandardBusinessDocumentUtils.getType(standardBusinessDocument).orElse(null);
        jsonDigitalPostSchemaValidator.validate(new Payload(payload).toJSONObject(), type);
        return standardBusinessDocument;
    }
}
