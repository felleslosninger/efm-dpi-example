
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;


@Data
public class Retur {

    AdresseInformasjon mottaker;
    Retur.Returposthaandtering returposthaandtering;

    public enum Returposthaandtering {

        DIREKTE_RETUR,
        MAKULERING_MED_MELDING
    }
}
