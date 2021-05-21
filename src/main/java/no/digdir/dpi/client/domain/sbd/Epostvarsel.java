
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Epostvarsel {

    String epostadresse;
    String varslingstekst;
    String spraak;
    List<Integer> repetisjoner = new ArrayList<>();
}
