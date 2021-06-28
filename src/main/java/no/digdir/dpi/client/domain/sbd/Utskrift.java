
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class Utskrift implements Message<Utskrift> {

    private Avsender avsender;
    private AdresseInformasjon mottaker;
    private Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk;
    private String maskinportentoken;

    private Utskriftstype utskriftstype;
    private Retur retur;
    private Posttype posttype;

    @Override
    public MessageType getMessageType() {
        return MessageType.UTSKRIFT;
    }

    public enum Posttype {
        A,
        B
    }

    public enum Utskriftstype {
        SORT_HVIT,
        FARGE
    }
}
