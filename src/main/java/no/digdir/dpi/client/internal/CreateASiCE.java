package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.AsicEAttachable;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.domain.Archive;
import no.digdir.dpi.client.internal.domain.ArchivedASiCE;
import no.digdir.dpi.client.internal.domain.Manifest;
import no.digdir.dpi.client.internal.domain.Signature;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateASiCE {

    private final CreateManifest createManifest;
    private final CreateSignature createSignature;
    private final CreateZip createZip;

    public ArchivedASiCE createAsice(Shipment shipment) {
        // Lag ASiC-E manifest
        log.info("Creating ASiC-E manifest");
        Manifest manifest = createManifest.createManifest(shipment);

        List<AsicEAttachable> files = new ArrayList<>();
        files.add(shipment.getParcel().getMainDocument());
        files.addAll(shipment.getParcel().getAttachments());
        Optional.ofNullable(shipment.getParcel().getMainDocument().getMetadataDocument()).ifPresent(files::add);
        files.add(manifest);

        // Lag signatur over alle filene i pakka
        Signature signature = createSignature.createSignature(files);
        files.add(signature);

        // Zip filene
        log.trace("Zipping ASiC-E files. Contains a total of " + files.size() + " files (including the generated manifest and signatures)");
        Archive archive = createZip.zipIt(files);

        return new ArchivedASiCE(archive.getBytes(), getUnzippedContentBytesCount(files));
    }

    static long getUnzippedContentBytesCount(List<AsicEAttachable> attachables) {
        return attachables.stream()
                .mapToLong(a -> a.getBytes().length)
                .sum();
    }
}
