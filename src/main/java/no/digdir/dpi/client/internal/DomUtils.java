package no.digdir.dpi.client.internal;

import lombok.SneakyThrows;
import no.digdir.dpi.client.exception.KonfigurasjonException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static javax.xml.xpath.XPathConstants.NODESET;

@Component
public class DomUtils {

    private final DocumentBuilderFactory documentBuilderFactory;
    private final TransformerFactory transformerFactory;

    DomUtils() {
        transformerFactory = TransformerFactory.newInstance();
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
    }


    public Document newEmptyXmlDocument() {
        try {
            return documentBuilderFactory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new KonfigurasjonException("Unable to create new Document. " + e.getClass().getSimpleName() + ": '" + e.getMessage() + "'", e);
        }
    }

    @SneakyThrows
    public Stream<Node> allNodesBelow(Node node) {
        NodeList nodeList = (NodeList) XPathFactory.newInstance().newXPath().evaluate(". | .//node() | .//@*", node, NODESET);
        return IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item);
    }

    public byte[] serializeToXml(Node root) {
        byte[] serializedXml;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(root), new StreamResult(outputStream));
            serializedXml = outputStream.toByteArray();
        } catch (TransformerException | IOException e) {
            throw new KonfigurasjonException("Unable to serialize XML because " + e.getClass().getSimpleName() + ": '" + e.getMessage(), e);
        }
        return serializedXml;
    }


}