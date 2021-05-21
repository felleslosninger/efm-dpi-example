
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;
import no.digdir.dpi.client.domain.Retur;

@Data
public class Fysiskpostinfo {

    AdresseInformasjon mottaker;
    Fysiskpostinfo.Utskriftstype utskriftstype;
    Retur retur;
    Fysiskpostinfo.Posttype posttype;

    public enum Posttype {
        A,
        B
    }

    public enum Utskriftstype {
        SORT_HVIT,
        FARGE
    }
}
