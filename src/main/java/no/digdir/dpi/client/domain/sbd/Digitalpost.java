
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class Digitalpost {

    Avsender avsender;
    Mottaker mottaker;
    Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk;
    String maskinportentoken;
    Digitalpostinfo digitalpostinfo;
    Fysiskpostinfo fysiskpostinfo;
}
