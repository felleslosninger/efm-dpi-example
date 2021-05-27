
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class ParcelFingerprint {

    String digestMethod;
    String digestValue;
}
