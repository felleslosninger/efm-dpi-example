package no.digdir.dpi.client.internal;

import lombok.Getter;
import no.digdir.dpi.client.domain.ReceivedMessage;

public interface ReceivedMessageValidator {
    void validate(ReceivedMessage receivedMessage);

    class Exception extends RuntimeException {

        @Getter
        private final ReceivedMessage receivedMessage;

        public Exception(ReceivedMessage receivedMessage, String message) {
            super(message);
            this.receivedMessage = receivedMessage;
        }
    }
}
