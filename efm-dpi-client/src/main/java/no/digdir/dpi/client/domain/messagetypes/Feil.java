
package no.digdir.dpi.client.domain.messagetypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import no.digdir.dpi.client.domain.sbd.Avsender;
import no.digdir.dpi.client.domain.sbd.Virksomhetmottaker;

import java.time.OffsetDateTime;

@Data
public class Feil implements BusinessMessage<Feil>, MaskinportentokenHolder {

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