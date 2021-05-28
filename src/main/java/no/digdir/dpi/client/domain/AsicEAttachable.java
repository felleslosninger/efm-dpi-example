package no.digdir.dpi.client.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface AsicEAttachable {
    String getFilename();

    byte[] getBytes();

    default InputStream getInputStream() {
        return new ByteArrayInputStream(getBytes());
    }

    String getMimeType();
}
