package no.digdir.dpi.client.domain.messagetypes;

public class Varslingfeiletkvittering extends AbstractKvittering<Varslingfeiletkvittering> {
    @Override
    public MessageType getMessageType() {
        return MessageType.VARSLINGFEILETKVITTERING;
    }
}
