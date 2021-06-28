package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import no.digdir.dpi.client.domain.sbd.header.DocumentIdentification;

import java.io.IOException;

public class TypeDeserializer extends JsonDeserializer<String> {

    static final String KEY = DocumentIdentification.class.getName() + ".type";

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String type = p.readValueAs(String.class);
        ctxt.setAttribute(KEY, MessageType.fromType(type));
        return type;
    }
}
