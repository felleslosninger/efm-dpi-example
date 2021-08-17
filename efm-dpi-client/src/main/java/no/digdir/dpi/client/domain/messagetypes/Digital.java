
package no.digdir.dpi.client.domain.messagetypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import no.digdir.dpi.client.domain.sbd.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class Digital implements BusinessMessage, AvsenderHolder, PersonmottakerHolder, DokumentpakkefingeravtrykkHolder, MaskinportentokenHolder {

    private Avsender avsender;
    private Personmottaker mottaker;
    private Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk;
    private String maskinportentoken;

    private Integer sikkerhetsnivaa;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate virkningsdato;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime virkningstidspunkt;
    private Boolean aapningskvittering = false;
    private Ikkesensitivtittel ikkesensitivtittel;
    private Varsler varsler;
}
