
package no.digdir.dpi.client.domain;

import lombok.Data;
import no.digdir.dpi.client.domain.sbd.AdresseInformasjon;


@Data
public class Retur {

    AdresseInformasjon mottaker;
    Retur.Returposthaandtering returposthaandtering;

    public enum Returposthaandtering {

        DIREKTE_RETUR,
        MAKULERING_MED_MELDING
    }
}
