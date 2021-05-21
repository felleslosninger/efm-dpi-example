
package no.digdir.dpi.client.domain.sbd;

import lombok.Data;

@Data
public class AdresseInformasjon {

    String navn;
    String adresselinje1;
    String adresselinje2;
    String adresselinje3;
    String adresselinje4;
    String postnummer;
    String poststed;
    String land;
    String landkode;
}
