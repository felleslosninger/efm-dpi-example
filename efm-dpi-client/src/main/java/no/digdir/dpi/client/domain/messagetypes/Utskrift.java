
package no.digdir.dpi.client.domain.messagetypes;

import lombok.Data;
import no.digdir.dpi.client.domain.sbd.AdresseInformasjon;
import no.digdir.dpi.client.domain.sbd.Avsender;
import no.digdir.dpi.client.domain.sbd.Dokumentpakkefingeravtrykk;
import no.digdir.dpi.client.domain.sbd.Retur;

@Data
public class Utskrift implements BusinessMessage<Utskrift>, DokumentpakkefingeravtrykkHolder, MaskinportentokenHolder {

    private Avsender avsender;
    private AdresseInformasjon mottaker;
    private Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk;
    private String maskinportentoken;

    private Utskriftstype utskriftstype;
    private Retur retur;
    private Posttype posttype;

    public enum Posttype {
        A,
        B
    }

    public enum Utskriftstype {
        SORT_HVIT,
        FARGE
    }
}
