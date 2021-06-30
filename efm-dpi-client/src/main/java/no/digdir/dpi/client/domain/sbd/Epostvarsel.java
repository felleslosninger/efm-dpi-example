
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Epostvarsel {

    private String epostadresse;
    private String varslingstekst;
    private String spraak;
    private List<Integer> repetisjoner = new ArrayList<>();
}
