
package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Feil implements Message<Feil> {

    private Avsender avsender;
    private Virksomhetmottaker mottaker;
    private String maskinportentoken;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime tidspunkt;
    private Type feiltype;
    private String detaljer;

    @Override
    public MessageType getMessageType() {
        return MessageType.FEIL;
    }

    private enum Type {
        KLIENT, SERVER
    }
}
