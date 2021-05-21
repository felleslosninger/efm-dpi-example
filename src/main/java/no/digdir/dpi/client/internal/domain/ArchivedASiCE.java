package no.digdir.dpi.client.internal.domain;

import lombok.Value;

@Value
public class ArchivedASiCE {

    byte[] bytes;
    long unzippedContentBytesCount;
}
