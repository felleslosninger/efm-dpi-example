package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.domain.Manifest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

@Component
@RequiredArgsConstructor
public class CreateManifest {

    private final Jaxb2Marshaller marshaller;
    private final SDPBuilder sdpBuilder;

    public Manifest createManifest(Shipment shipment) {
        SDPManifest sdpManifest = sdpBuilder.createManifest(shipment);

        ByteArrayOutputStream manifestStream = new ByteArrayOutputStream();
        try {
            marshaller.marshal(sdpManifest, new StreamResult(manifestStream));
            return new Manifest(new ByteArrayResource(manifestStream.toByteArray()));
        } catch (MarshallingFailureException e) {
            if (e.getMostSpecificCause() instanceof SAXParseException) {
                throw new Exception("Kunne ikke validere generert Manifest XML. Sjekk at alle påkrevde input er satt og ikke er null",
                        e.getMostSpecificCause());
            }

            throw e;
        }
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
