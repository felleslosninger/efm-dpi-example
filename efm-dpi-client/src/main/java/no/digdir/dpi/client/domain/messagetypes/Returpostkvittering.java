package no.digdir.dpi.client.domain.messagetypes;

public class Returpostkvittering extends AbstractKvittering<Returpostkvittering> {
    @Override
    public MessageType getMessageType() {
        return MessageType.RETURPOSTKVITTERING;
    }
}
