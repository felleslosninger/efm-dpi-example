
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class Digitalpost {

    Avsender avsender;
    Mottaker mottaker;
    ParcelFingerprint parcelFingerprint;
    String maskinportentoken;
    Digitalpostinfo digitalpostinfo;
    Fysiskpostinfo fysiskpostinfo;
}
