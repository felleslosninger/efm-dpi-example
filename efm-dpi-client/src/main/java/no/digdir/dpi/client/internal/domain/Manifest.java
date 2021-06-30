package no.digdir.dpi.client.internal.domain;


import lombok.Value;
import no.digdir.dpi.client.domain.AsicEAttachable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

@Value
public class Manifest implements AsicEAttachable {

    byte[] bytes;

    @Override
    public String getFilename() {
        return "manifest.xml";
    }

    @Override
    public Resource getResource() {
        return new ByteArrayResource(bytes);
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }
}
