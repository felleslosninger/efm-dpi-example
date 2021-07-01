package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import no.digdir.dpi.client.domain.messagetypes.MessageType;
import no.digdir.dpi.client.domain.sbd.header.StandardBusinessDocumentHeader;
import org.springframework.util.Assert;

import java.io.IOException;

public class StandardBusinessDocumentDeserializer extends JsonDeserializer<StandardBusinessDocument> {

    @Override
    public Class<?> handledType() {
        return StandardBusinessDocument.class;
    }

    @Override
    public StandardBusinessDocument deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        StandardBusinessDocumentHeader header = readObject(p, "standardBusinessDocumentHeader", StandardBusinessDocumentHeader.class);
        MessageType type = MessageType.fromType(header.getType());
        StandardBusinessDocument standardBusinessDocument = new StandardBusinessDocument()
                .setStandardBusinessDocumentHeader(header)
                .setBusinessMessage(readObject(p, type.getType(), type.getClazz()));
        assertToken(p, JsonToken.END_OBJECT);
        return standardBusinessDocument;
    }

    private <T> T readObject(JsonParser p, String fieldName, Class<T> valueType) throws IOException {
        assertFieldName(p, fieldName);
        assertToken(p, JsonToken.START_OBJECT);
        return p.readValueAs(valueType);
    }

    private void assertFieldName(JsonParser parser, String expected) throws IOException {
        assertToken(parser, JsonToken.FIELD_NAME);
        Assert.isTrue(parser.getCurrentName().equals(expected), String.format("Expected to find field named %s, but found %s", expected, parser.getCurrentName()));
    }

    private void assertToken(JsonParser parser, JsonToken expected) throws IOException {
        JsonToken token = parser.nextToken();
        Assert.isTrue(token == expected, String.format("Expected token %s, but found %s", expected, token));
    }
}
