
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class Avsender {

    Identifikator virksomhetsidentifikator;
    String avsenderidentifikator;
    String fakturaReferanse;
}
