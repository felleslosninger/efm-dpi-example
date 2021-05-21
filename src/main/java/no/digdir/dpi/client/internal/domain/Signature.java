package no.digdir.dpi.client.internal.domain;

import no.digdir.dpi.client.domain.AsicEAttachable;

public class Signature implements AsicEAttachable {

    private final byte[] xmlBytes;

    public Signature(byte[] xmlBytes) {
        this.xmlBytes = xmlBytes;
    }

    @Override
    public String getFileName() {
        return "META-INF/signatures.xml";
    }

    public byte[] getBytes() {
        return xmlBytes;
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }
}
