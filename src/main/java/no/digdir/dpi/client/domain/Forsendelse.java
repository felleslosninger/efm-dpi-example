package no.digdir.dpi.client.domain;

import lombok.Data;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;

@Data
public class Forsendelse {

    StandardBusinessDocument standardBusinessDocument;
    Dokumentpakke dokumentpakke;
    String postkasseadresse;
    Sertifikat mottakerSertifikat;
    String spraakkode = "NO";
}
