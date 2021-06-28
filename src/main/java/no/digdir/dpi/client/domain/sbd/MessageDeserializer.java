package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class MessageDeserializer extends JsonDeserializer<Message<? extends Message<?>>> {

    @Override
    public Message<? extends Message<?>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        MessageType type = (MessageType) ctxt.getAttribute(TypeDeserializer.KEY);

        if (type == null || !type.getType().equals(p.getCurrentName())) {
            return null;
        }

        return p.readValueAs(type.getClazz());
    }
}
