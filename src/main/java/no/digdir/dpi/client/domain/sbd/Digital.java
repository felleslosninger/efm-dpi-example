
package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class Digital implements Message<Digital> {

    private Avsender avsender;
    private Personmottaker mottaker;
    private Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk;
    private String maskinportentoken;

    private Integer sikkerhetsnivaa;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate virkningsdato;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime virkningstidspunkt;
    private boolean aapningskvittering = false;
    private Ikkesensitivtittel ikkesensitivtittel;
    private Varsler varsler;

    @Override
    public MessageType getMessageType() {
        return MessageType.DIGITAL;
    }
}
