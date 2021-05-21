
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

import java.util.Date;

@Data
public class Digitalpostinfo {

    Integer sikkerhetsnivaa;
    String virkningsdato;
    Date virkningstidspunkt;
    boolean aapningskvittering = false;
    Ikkesensitivtittel ikkesensitivtittel;
    Varsler varsler;
}
