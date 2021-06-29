package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import no.digdir.dpi.client.domain.messagetypes.BusinessMessage;
import no.digdir.dpi.client.domain.messagetypes.MessageType;

import java.io.IOException;

public class MessageDeserializer extends JsonDeserializer<BusinessMessage<? extends BusinessMessage<?>>> {

    @Override
    public BusinessMessage<? extends BusinessMessage<?>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        MessageType type = (MessageType) ctxt.getAttribute(TypeDeserializer.KEY);

        if (type == null || !type.getType().equals(p.getCurrentName())) {
            return null;
        }

        return p.readValueAs(type.getClazz());
    }
}
