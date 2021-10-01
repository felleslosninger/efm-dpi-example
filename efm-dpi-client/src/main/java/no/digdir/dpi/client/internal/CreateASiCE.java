package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.asic.*;
import no.difi.move.common.cert.KeystoreHelper;
import no.digdir.dpi.client.domain.AsicEAttachable;
import no.digdir.dpi.client.domain.KeyPair;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.domain.Manifest;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateASiCE {

    private final CreateManifest createManifest;
    private final SignatureHelper signatureHelper;

    public void createAsice(Shipment shipment, OutputStream outputStream) {
        log.info("Creating ASiC-E manifest");
        Manifest manifest = createManifest.createManifest(shipment);

        AsicWriter asicWriter = getAsicWriter(outputStream);

        addAsicFile(asicWriter, manifest);
        addAsicFile(asicWriter, shipment.getParcel().getMainDocument());
        shipment.getParcel().getAttachments().forEach(p -> addAsicFile(asicWriter, p));
        Optional.ofNullable(shipment.getParcel().getMainDocument().getMetadataDocument()).ifPresent(p -> addAsicFile(asicWriter, p));

        sign(asicWriter);
    }

    private void sign(AsicWriter asicWriter)  {
        try {
            asicWriter.sign(signatureHelper);
        } catch (IOException e) {
            throw new CreateASiCE.Exception("Could not sign ASiC-E!", e);
        }
    }

    private void addAsicFile(AsicWriter asicWriter, AsicEAttachable attachable) {
        log.debug("Adding file {} of type {}", attachable.getFilename(), attachable.getMimeType());
        try (InputStream inputStream = new BufferedInputStream(attachable.getResource().getInputStream())) {
            asicWriter.add(inputStream, attachable.getFilename(), MimeType.forString(attachable.getMimeType()));
        } catch (IOException e) {
            throw new CreateASiCE.Exception("Could not add manifest to ASiC-E!", e);
        }
    }

    private AsicWriter getAsicWriter(OutputStream outputStream) {
        try {
            return AsicWriterFactory.newFactory(SignatureMethod.XAdES)
                    .newContainer(outputStream);
        } catch (IOException e) {
            throw new CreateASiCE.Exception("Could not create ASiC-E writer!", e);
        }
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
