package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ManifestParser {

    private final Jaxb2Marshaller marshaller;

    public SDPManifest parse(Resource resource) {
        try(InputStream inputStream = resource.getInputStream()) {
            return (SDPManifest) marshaller.unmarshal(new StreamSource(inputStream));
        } catch (IOException e) {
            throw new ManifestParser.Exception("Failed to parse SDPManifest!", e);
        }
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
