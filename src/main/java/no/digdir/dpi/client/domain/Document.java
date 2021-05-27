package no.digdir.dpi.client.domain;

import lombok.Data;

@Data
public class Document implements AsicEAttachable {

    String title;
    String filename;
    byte[] bytes;
    String mimeType;
    MetadataDocument metadataDocument;
}
