package no.digdir.dpi.client.domain;

import lombok.Data;

import java.util.List;

@Data
public class Dokumentpakke {

    Dokument hoveddokument;
    List<Dokument> vedlegg;
}