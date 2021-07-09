package no.digdir.dpi.client.internal;

import com.nimbusds.jose.Payload;
import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnpackStandardBusinessDocument {

    private final JsonDigitalPostSchemaValidator jsonDigitalPostSchemaValidator;
    private final DpiMapper dpiMapper;

    public StandardBusinessDocument unpackStandardBusinessDocument(Payload payload) {
        StandardBusinessDocument standardBusinessDocument = dpiMapper.readStandardBusinessDocument(payload.toString());
        jsonDigitalPostSchemaValidator.validate(payload.toJSONObject(), standardBusinessDocument.getType());
        return standardBusinessDocument;
    }
}
