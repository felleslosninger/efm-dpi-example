
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class Avsender {

    private Identifikator virksomhetsidentifikator;
    private String avsenderidentifikator;
    private String fakturaReferanse;
}
