package no.digdir.dpi.client.domain;

import lombok.Data;

@Data
public class MetadataDocument implements AsicEAttachable {

    String filename;
    byte[] bytes;
    String mimeType;
}
