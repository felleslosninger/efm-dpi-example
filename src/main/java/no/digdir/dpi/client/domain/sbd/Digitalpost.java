
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class Digitalpost {

    private Avsender avsender;
    private Mottaker mottaker;
    private Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk;
    private String maskinportentoken;
    private Digitalpostinfo digitalpostinfo;
    private Fysiskpostinfo fysiskpostinfo;
}
