package no.digdir.dpi.client.domain;

import lombok.Data;

@Data
public class MetadataDokument implements AsicEAttachable {

    String filnavn;
    byte[] dokument;
    String mimeType;

    @Override
    public String getFileName() {
        return filnavn;
    }

    @Override
    public byte[] getBytes() {
        return dokument;
    }
}
