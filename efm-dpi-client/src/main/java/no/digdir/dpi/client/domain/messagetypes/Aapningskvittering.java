package no.digdir.dpi.client.domain.messagetypes;

public class Aapningskvittering extends AbstractKvittering {

    @Override
    public MessageType getMessageType() {
        return MessageType.AAPNINGSKVITTERING;
    }
}
