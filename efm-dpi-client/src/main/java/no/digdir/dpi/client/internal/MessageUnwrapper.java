package no.digdir.dpi.client.internal;

import com.nimbusds.jose.Payload;
import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.digdir.dpi.client.domain.Message;
import no.digdir.dpi.client.domain.ReceivedMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageUnwrapper {

    private final UnpackJWT unpackJWT;
    private final UnpackStandardBusinessDocument unpackStandardBusinessDocument;
    private final ReceivedMessageValidator receivedMessageValidator;

    public ReceivedMessage unwrap(Message message) {
        ReceivedMessage receivedMessage = new ReceivedMessage()
                .setMessage(message)
                .setStandardBusinessDocument(getStandardBusinessDocument(message));
        receivedMessageValidator.validate(receivedMessage);
        return receivedMessage;
    }

    private StandardBusinessDocument getStandardBusinessDocument(Message message) {
        Payload payload = unpackJWT.getPayload(message.getForettningsmelding());
        return unpackStandardBusinessDocument.unpackStandardBusinessDocument(payload);
    }
}
