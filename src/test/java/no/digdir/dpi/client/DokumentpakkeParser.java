package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import no.difi.begrep.sdp.schema_v10.SDPDokument;
import no.difi.begrep.sdp.schema_v10.SDPDokumentData;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.digdir.dpi.client.domain.Dokument;
import no.digdir.dpi.client.domain.Dokumentpakke;
import no.digdir.dpi.client.domain.MetadataDokument;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DokumentpakkeParser {

    private final AsicParser asicParser;
    private final ManifestParser manifestParser;

    public Dokumentpakke parse(InputStream asicInputStream) {
        Map<String, Dokument> documents = new HashMap<>();
        asicParser.parse(asicInputStream,
                ((filename, inputStream) -> documents.put(filename, getDocument(filename, inputStream))));

        SDPManifest manifest = manifestParser.parse(new ByteArrayInputStream(
                getDocument(documents, "manifest.xml")
                        .getDokument()));

        return new Dokumentpakke()
                .setHoveddokument(getDocument(documents, manifest.getHoveddokument()))
                .setVedlegg(manifest.getVedleggs().stream()
                        .map(p -> getDocument(documents, p))
                        .collect(Collectors.toList())
                );
    }

    private Dokument getDocument(Map<String, Dokument> documents, String filename) {
        return Optional.ofNullable(documents.get(filename))
                .orElseThrow(() -> new Exception(String.format("No file named '%s' in ASICe!", filename)));
    }

    private Dokument getDocument(Map<String, Dokument> documents, SDPDokument sdpDokument) {
        return getDocument(documents, sdpDokument.getHref())
                .setMimeType(sdpDokument.getMime())
                .setTittel(sdpDokument.getTittel().getValue())
                .setMetadataDocument(getMetadataDocument(sdpDokument.getData()));
    }

    private MetadataDokument getMetadataDocument(SDPDokumentData data) {
        if (data == null) {
            return null;
        }
        return new MetadataDokument()
                .setFilnavn(data.getHref())
                .setMimeType(data.getMime());
    }

    private Dokument getDocument(String filename, InputStream inputStream) {
        try {
            return new Dokument()
                    .setFilnavn(filename)
                    .setDokument(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new Exception(String.format("Couldn't read file named '%s", filename), e);
        }
    }

    private static class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }

        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
