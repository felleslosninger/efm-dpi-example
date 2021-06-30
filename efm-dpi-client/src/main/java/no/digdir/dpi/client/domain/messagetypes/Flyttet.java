package no.digdir.dpi.client.domain.messagetypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import no.digdir.dpi.client.domain.sbd.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class Flyttet implements BusinessMessage<Flyttet>, DokumentpakkefingeravtrykkHolder, MaskinportentokenHolder {

    private Avsender avsender;
    private Personmottaker mottaker;
    private String maskinportentoken;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime tidspunkt;
    private Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate mottakstidspunkt;
    private Boolean aapnet;
    private Integer sikkerhetsnivaa;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate virkningsdato;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime virkningstidspunkt;
    private Boolean aapningskvittering;
    private Ikkesensitivtittel ikkesensitivtittel;
    private Varsler varsler;

    @Override
    public MessageType getMessageType() {
        return MessageType.FLYTTET;
    }
}
