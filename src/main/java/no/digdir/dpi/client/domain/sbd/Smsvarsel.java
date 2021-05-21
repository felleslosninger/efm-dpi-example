
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Smsvarsel {

    String mobiltelefonnummer;
    String varslingstekst;
    String spraak;
    List<Integer> repetisjoner = new ArrayList<>();
}
