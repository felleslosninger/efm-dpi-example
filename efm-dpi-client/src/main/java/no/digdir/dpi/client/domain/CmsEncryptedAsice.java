package no.digdir.dpi.client.domain;

import lombok.Value;
import no.digdir.dpi.client.internal.InMemoryWithTempFileFallbackResource;

@Value
public class CmsEncryptedAsice implements AutoCloseable {

    InMemoryWithTempFileFallbackResource resource;

    @Override
    public void close() throws Exception {
        resource.deleteFileIfItExists();
    }
}
