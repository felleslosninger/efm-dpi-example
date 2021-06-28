package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StandardBusinessDocumentSerializer extends JsonSerializer<StandardBusinessDocument> {
    @Override
    public void serialize(StandardBusinessDocument value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("standardBusinessDocumentHeader");
        gen.writeObject(value.getStandardBusinessDocumentHeader());
        gen.writeFieldName(value.getMessage().getMessageType().getType());
        gen.writeObject(value.getMessage());
        gen.writeEndObject();
    }
}
