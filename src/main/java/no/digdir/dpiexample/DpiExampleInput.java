package no.digdir.dpiexample;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DpiExampleInput {

    String avsender;
    String mottaker;
    String postkasseadresse;
    String orgnrPostkasse;
    String sertifikat;
    String tittel;
    List<String> files;
}
