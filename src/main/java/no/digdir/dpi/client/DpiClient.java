package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import network.oxalis.api.lang.OxalisContentException;
import network.oxalis.api.lang.OxalisTransmissionException;
import network.oxalis.api.outbound.TransmissionRequest;
import network.oxalis.api.outbound.TransmissionResponse;
import network.oxalis.outbound.OxalisOutboundComponent;
import network.oxalis.outbound.transmission.TransmissionRequestBuilder;
import network.oxalis.sniffer.sbdh.SbdhWrapper;
import network.oxalis.vefa.peppol.common.model.DocumentTypeIdentifier;
import network.oxalis.vefa.peppol.common.model.Endpoint;
import network.oxalis.vefa.peppol.common.model.Header;
import network.oxalis.vefa.peppol.common.model.InstanceIdentifier;
import network.oxalis.vefa.peppol.common.model.InstanceType;
import network.oxalis.vefa.peppol.common.model.ParticipantIdentifier;
import network.oxalis.vefa.peppol.common.model.ProcessIdentifier;
import network.oxalis.vefa.peppol.common.model.TransportProfile;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.CreateCmsEncryptedAsice;
import no.digdir.dpi.client.internal.CreateJWT;
import no.digdir.dpi.client.internal.CreateMaskinportenToken;
import no.digdir.dpi.client.internal.CreateParcelFingerprint;
import no.digdir.dpi.client.internal.StandBusinessDocumentJsonFinalizer;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DpiClient {

    private final CreateCmsEncryptedAsice createCmsEncryptedAsice;
    private final CreateMaskinportenToken createMaskinportenToken;
    private final CreateParcelFingerprint createParcelFingerprint;
    private final StandBusinessDocumentJsonFinalizer standBusinessDocumentJsonFinalizer;
    private final CreateJWT createJWT;
    private final OxalisOutboundComponent oxalisOutboundComponent;
    private final SbdhWrapper sbdhWrapper;

    @SneakyThrows
    public void send(Shipment shipment) {
        try (CmsEncryptedAsice cmsEncryptedAsice = createCmsEncryptedAsice.createCmsEncryptedAsice(shipment)) {
            String maskinportenToken = createMaskinportenToken.getMaskinportenToken();

            Map<String, Object> finalizedSBD = standBusinessDocumentJsonFinalizer.getFinalizedStandardBusinessDocumentAsJson(
                    shipment.getStandardBusinessDocument(),
                    createParcelFingerprint.createParcelFingerprint(cmsEncryptedAsice),
                    maskinportenToken);

            String jwt = createJWT.createJWT(finalizedSBD);
            
            String binaryContent = Base64.getEncoder().encodeToString(
                IOUtils.toByteArray(
                    cmsEncryptedAsice.getResource().getInputStream()
                )
            );
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(Digitalpost.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(new Digitalpost(jwt, binaryContent), baos);
            
            TransmissionRequest request = createTransmissionRequest(
                new RequestParameters(
                    "0192:123456789",
                    "0192:987654321",
                    new ByteArrayInputStream(baos.toByteArray()),
                    URI.create("http://localhost:8082/as4"), // Dette er URL mot din lokale oxalis-inbound
                    getDestCert(),
                    // todo: Digitalpost dokumenttype
                    DocumentTypeIdentifier.of("${CreditNote.CREDIT_NOTE_NAMESPACE}::CreditNote##urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0::2.1"),
                    ProcessIdentifier.of("urn:fdc:peppol.eu:2017:poacc:billing:01:1.0"),
                    InstanceType.of(CREDIT_NOTE_NAMESPACE, "CreditNote", "2.1"),
                    TransportProfile.PEPPOL_AS4_2_0
                )
            );

            TransmissionResponse response = oxalisOutboundComponent.getTransmitter().transmit(request);
        }
    }
    
    private X509Certificate getDestCert() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        // todo: legg inn filsti til peppol_keystore.p12
        InputStream inStream = new FileInputStream("your_string_here");

        KeyStore ks = KeyStore.getInstance("PKCS12");
        // todo: legg inn passord for oxalis-keystore
        ks.load(inStream, "your_string_here".toCharArray());

        String alias = ks.aliases().nextElement();
        return (X509Certificate) ks.getCertificate(alias);
    }

    private TransmissionRequest createTransmissionRequest(RequestParameters parameters) throws OxalisContentException, OxalisTransmissionException {
        TransmissionRequestBuilder requestBuilder = oxalisOutboundComponent.getTransmissionRequestBuilder();
        requestBuilder.setTransmissionBuilderOverride(true);

        requestBuilder.receiver(ParticipantIdentifier.of(parameters.recipient));
        requestBuilder.sender(ParticipantIdentifier.of(parameters.sender));
        requestBuilder.documentType(parameters.documentType);
        requestBuilder.processType(parameters.profileType);
        requestBuilder.payLoad(addStandardBusinessDocumentHeaderToDpiPayload(parameters));

        if (parameters.destinationUri != null && parameters.destinationCertificate != null) {
            Endpoint endpoint = Endpoint.of(parameters.protocol, parameters.destinationUri, parameters.destinationCertificate);
            requestBuilder.overrideAs2Endpoint(endpoint);
        }

        return requestBuilder.build();
    }
    
    private InputStream addStandardBusinessDocumentHeaderToDpiPayload(RequestParameters parameters) {
        InstanceIdentifier instanceIdentifier = InstanceIdentifier.generateUUID();
        Date creationDate = Date.from(Instant.now());
        Header sbdhHeader = Header.of(
            ParticipantIdentifier.of(parameters.sender),
            ParticipantIdentifier.of(parameters.recipient),
            parameters.profileType,
            parameters.documentType,
            instanceIdentifier,
            parameters.instanceType,
            creationDate
        );
        return new ByteArrayInputStream(sbdhWrapper.wrap(parameters.payload, sbdhHeader));
    }

    public String CREDIT_NOTE_NAMESPACE = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2";

    public class RequestParameters {
        public String recipient;
        public String sender;
        public URI destinationUri;
        public X509Certificate destinationCertificate;
        public DocumentTypeIdentifier documentType;
        public ProcessIdentifier profileType;
        public InstanceType instanceType;
        public InputStream payload;
        public TransportProfile protocol;
        
        public RequestParameters(
                String recipient,
                String sender,
                InputStream payload,
                URI destinationUri,
                X509Certificate destinationCertificate,
                DocumentTypeIdentifier documentType,
                ProcessIdentifier profileType,
                InstanceType instanceType,
                TransportProfile protocol) {
            this.recipient = recipient;
            this.sender = sender;
            this.payload = payload;
            this.destinationUri = destinationUri;
            this.destinationCertificate = destinationCertificate;
            this.documentType = documentType;
            this.profileType = profileType;
            this.instanceType = instanceType;
            this.protocol = protocol;
        }
    }
}
