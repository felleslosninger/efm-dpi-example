
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Smsvarsel {

    private String mobiltelefonnummer;
    private String varslingstekst;
    private String spraak;
    private List<Integer> repetisjoner = new ArrayList<>();
}
