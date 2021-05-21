package no.digdir.dpi.client.internal.domain;


import lombok.Value;
import no.digdir.dpi.client.domain.AsicEAttachable;

@Value
public class Manifest implements AsicEAttachable {

    byte[] xmlBytes;

    @Override
    public String getFileName() {
        return "manifest.xml";
    }

    @Override
    public byte[] getBytes() {
        return xmlBytes;
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }
}
