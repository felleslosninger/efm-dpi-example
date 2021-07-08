package no.digdir.dpi.client.domain.messagetypes;

public class Leveringskvittering extends AbstractKvittering {

    @Override
    public MessageType getMessageType() {
        return MessageType.LEVERINGSKVITTERING;
    }
}
