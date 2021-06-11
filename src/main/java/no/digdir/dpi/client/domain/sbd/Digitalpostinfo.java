
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

import java.util.Date;

@Data
public class Digitalpostinfo {

    private Integer sikkerhetsnivaa;
    private String virkningsdato;
    private Date virkningstidspunkt;
    private boolean aapningskvittering = false;
    private Ikkesensitivtittel ikkesensitivtittel;
    private Varsler varsler;
}
