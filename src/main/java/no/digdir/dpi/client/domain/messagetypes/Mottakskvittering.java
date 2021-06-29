package no.digdir.dpi.client.domain.messagetypes;

public class Mottakskvittering extends AbstractKvittering<Mottakskvittering> {
    @Override
    public MessageType getMessageType() {
        return MessageType.MOTTAKSKVITTERING;
    }
}
