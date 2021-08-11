package no.digdir.dpi.client.internal;

import lombok.Value;
import no.digdir.dpi.client.etsi.QualifyingProperties;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.dom.DOMResult;

import static java.util.stream.IntStream.range;

@Value
class XAdESArtifacts {

    private static final Jaxb2Marshaller MARSHALLER;

    static {
        MARSHALLER = new Jaxb2Marshaller();
        MARSHALLER.setClassesToBeBound(QualifyingProperties.class);
    }

    Document document;
    Element signableProperties;
    String signablePropertiesReferenceUri;

    public static XAdESArtifacts from(QualifyingProperties qualifyingProperties) {
        DOMResult domResult = new DOMResult();
        MARSHALLER.marshal(qualifyingProperties, domResult);
        return from((Document) domResult.getNode());
    }

    private static XAdESArtifacts from(Document qualifyingPropertiesDocument) {
        Element qualifyingProperties = qualifyingPropertiesDocument.getDocumentElement();
        NodeList qualifyingPropertiesContents = qualifyingProperties.getChildNodes();
        Element signedProperties = range(0, qualifyingPropertiesContents.getLength()).mapToObj(qualifyingPropertiesContents::item)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast)
                .filter(element -> "SignedProperties".equals(element.getLocalName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Didn't find SignedProperties in document."));
        String signerPropertiesReferenceUri = signedProperties.getAttribute("Id");
        return new XAdESArtifacts(qualifyingPropertiesDocument, signedProperties, "#" + signerPropertiesReferenceUri);
    }
}