
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class Fysiskpostinfo {

    private AdresseInformasjon mottaker;
    private Fysiskpostinfo.Utskriftstype utskriftstype;
    private Retur retur;
    private Fysiskpostinfo.Posttype posttype;

    public enum Posttype {
        A,
        B
    }

    public enum Utskriftstype {
        SORT_HVIT,
        FARGE
    }
}
