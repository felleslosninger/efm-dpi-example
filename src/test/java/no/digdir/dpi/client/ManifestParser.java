package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ManifestParser {

    private final Jaxb2Marshaller marshaller;

    public SDPManifest parse(InputStream inputStream) {
        return (SDPManifest) marshaller.unmarshal(new StreamSource(inputStream));
    }
}
