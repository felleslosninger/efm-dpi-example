package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.digdir.dpi.client.domain.Forsendelse;
import no.digdir.dpi.client.exception.KonfigurasjonException;
import no.digdir.dpi.client.exception.SendException;
import no.digdir.dpi.client.exception.XmlValideringException;
import no.digdir.dpi.client.internal.domain.Manifest;
import no.digipost.api.xml.Schemas;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

@Component
@RequiredArgsConstructor
public class CreateManifest {

    private static final Jaxb2Marshaller marshaller;

    static {
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(SDPManifest.class);
        marshaller.setSchema(Schemas.SDP_MANIFEST_SCHEMA);
        try {
            marshaller.afterPropertiesSet();
        } catch (Exception e) {
            throw new KonfigurasjonException("Kunne ikke sette opp Jaxb marshaller", e);
        }
    }

    private final SDPBuilder sdpBuilder;

    public Manifest createManifest(Forsendelse forsendelse) {
        SDPManifest sdpManifest = sdpBuilder.createManifest(forsendelse);

        ByteArrayOutputStream manifestStream = new ByteArrayOutputStream();
        try {
            marshaller.marshal(sdpManifest, new StreamResult(manifestStream));
            return new Manifest(manifestStream.toByteArray());
        } catch (MarshallingFailureException e) {
            if (e.getMostSpecificCause() instanceof SAXParseException) {
                throw new XmlValideringException("Kunne ikke validere generert Manifest XML. Sjekk at alle påkrevde input er satt og ikke er null",
                        SendException.AntattSkyldig.KLIENT, (SAXParseException) e.getMostSpecificCause());
            }

            throw e;
        }

    }
}
