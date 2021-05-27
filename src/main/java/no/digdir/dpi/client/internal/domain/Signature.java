package no.digdir.dpi.client.internal.domain;

import lombok.Value;
import no.digdir.dpi.client.domain.AsicEAttachable;

@Value
public class Signature implements AsicEAttachable {

    byte[] bytes;

    @Override
    public String getFilename() {
        return "META-INF/signatures.xml";
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }
}
