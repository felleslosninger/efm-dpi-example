package no.digdir.dpi.client.domain;

import lombok.Data;

@Data
public class Dokument implements AsicEAttachable {

    String tittel;
    String filnavn;
    byte[] dokument;
    String mimeType;
    MetadataDokument metadataDocument;

    @Override
    public String getFileName() {
        return filnavn;
    }

    @Override
    public byte[] getBytes() {
        return dokument;
    }
}
