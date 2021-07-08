package no.digdir.dpi.client.domain.messagetypes;

public class Mottakskvittering extends AbstractKvittering {
    @Override
    public MessageType getMessageType() {
        return MessageType.MOTTAKSKVITTERING;
    }
}
