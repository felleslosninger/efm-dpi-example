package no.digdir.dpi.client;


import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.X509CertChainUtils;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.core.Option;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentUtils;
import no.digdir.dpi.client.domain.*;
import no.digdir.dpi.client.internal.DpiMapper;
import no.digdir.dpi.client.internal.JsonDigitalPostSchemaValidator;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.RequestDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.util.SharedByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.core.ConfigurationWhen.paths;
import static net.javacrumbs.jsonunit.core.ConfigurationWhen.then;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ContextConfiguration(
        initializers = ConfigFileApplicationContextInitializer.class,
        classes = {DpiClientTestConfig.class, DpiClientConfig.class})
@ExtendWith({SpringExtension.class, MockServerExtension.class})
@MockServerSettings(ports = 8900)
class DpiClientTest {

    @Autowired
    private DpiClient dpiClient;

    @Autowired
    private ShipmentFactory shipmentFactory;

    @Autowired
    private DecryptCMSDocument decryptCMSDocument;

    @Autowired
    private ParcelParser parcelParser;

    @Autowired
    private DpiMapper dpiMapper;

    @Autowired
    private JsonDigitalPostSchemaValidator jsonDigitalPostSchemaValidator;

    @Value("classpath:/digital-sbd.json")
    private Resource digitalSbd;

    @Value("classpath:/digital_ready_for_send-sbd.json")
    private Resource digitalReadyForSendSbd;

    @Value("classpath:/utskrift-sbd.json")
    private Resource utskriftSbd;

    @Value("classpath:/utskrift_ready_for_send-sbd.json")
    private Resource utskriftReadyForSendSbd;

    @Value("classpath:/svada.pdf")
    private Resource hoveddokument;

    @Value("classpath:/bilde.png")
    private Resource vedlegg;

    @Value("classpath:/c2.cer")
    private Resource sertifikat;

    @Value("classpath:/message_statuses.json")
    private Resource messageStatusesResource;

    @Value("classpath:/messages.json")
    private Resource messagesResource;

    @BeforeEach
    public void beforeEach(MockServerClient client) {
        client.when(request()
                .withMethod("POST")
                .withPath("/token"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody("{ \"access_token\" : \"DummyMaskinportenToken\" }")
                );
    }

    @AfterEach
    public void afterEach(MockServerClient client) {
        client.reset();
    }

    @Test
    void testSendDigital(MockServerClient client) {
        testSend(client, digitalSbd, digitalReadyForSendSbd);
    }

    @Test
    void testSendFysisk(MockServerClient client) {
        testSend(client, utskriftSbd, utskriftReadyForSendSbd);
    }

    @Test
    void testSendWhenResponseError(MockServerClient client) {
        HttpResponse httpResponse = response()
                .withBody("{}")
                .withStatusCode(400);

        assertThatThrownBy(() -> send(client, digitalSbd, httpResponse))
                .isInstanceOf(DpiException.class)
                .hasMessage("400 Bad Request from POST http://localhost:8900/dpi/send")
                .hasCauseInstanceOf(WebClientResponseException.BadRequest.class);
    }

    @Test
    void testGetMessageStatuses(MockServerClient client) {
        UUID uuid = UUID.randomUUID();

        client.when(request()
                .withMethod("GET")
                .withPath(String.format("/dpi/statuses/%s", uuid)))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ResourceUtils.toByteArray(messageStatusesResource))
                );

        StepVerifier.create(dpiClient.getMessageStatuses(uuid))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .consumeRecordedWith(elements -> assertThat(elements)
                        .containsExactly(
                                new MessageStatus()
                                        .setStatus(ReceiptStatus.SENDT)
                                        .setTimestamp(OffsetDateTime.parse("2021-06-29T05:49:47Z")),
                                new MessageStatus()
                                        .setStatus(ReceiptStatus.MOTTATT)
                                        .setTimestamp(OffsetDateTime.parse("2021-06-29T07:12:40Z"))
                        ))
                .verifyComplete();

        client.verify(request()
                .withMethod("GET")
                .withPath(String.format("/dpi/statuses/%s", uuid)));
    }

    @Test
    void testGetMessages(MockServerClient client) {
        client.when(request()
                .withMethod("GET")
                .withPath("/dpi/messages"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ResourceUtils.toByteArray(messagesResource))
                );

        StepVerifier.create(dpiClient.getMessages())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .consumeRecordedWith(elements -> assertThat(elements).containsExactly(new Message()
                        .setForettningsmelding("{ \"key\": \"value\" }")
                        .setDownloadurl(URI.create("http://localhost:8900/dpi/downloadmessage/a9bc8498-13b1-4cef-9cf9-4873a03b484d"))
                ))
                .verifyComplete();

        client.verify(request()
                .withMethod("GET")
                .withPath("/dpi/messages"));
    }

    @Test
    void testGetCmsEncryptedAsice(MockServerClient client) {
        UUID uuid = UUID.randomUUID();
        String path = String.format("/dpi/downloadmessage/%s", uuid);
        byte[] bytes = new byte[1024 * 100];
        ThreadLocalRandom.current().nextBytes(bytes);

        client.when(request()
                .withMethod("GET")
                .withPath(path))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(bytes)
                );

        CmsEncryptedAsice cmsEncryptedAsice = dpiClient.getCmsEncryptedAsice(URI.create("http://localhost:8900" + path));

        assertThat(cmsEncryptedAsice.getResource().contentLength()).isEqualTo(1024 * 100);
        assertThat(ResourceUtils.toByteArray(cmsEncryptedAsice.getResource())).isEqualTo(bytes);

        client.verify(request()
                .withMethod("GET")
                .withPath(path));
    }

    @Test
    void testMarkAsRead(MockServerClient client) {
        UUID uuid = UUID.randomUUID();
        String path = String.format("/dpi/setmessageread/%s", uuid);

        client.when(request()
                .withMethod("POST")
                .withPath(path))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(ResourceUtils.toByteArray(messagesResource))
                );

        dpiClient.markAsRead(uuid);

        client.verify(request()
                .withMethod("POST")
                .withPath(path));
    }

    @SneakyThrows
    private void testSend(MockServerClient client, Resource in, Resource out) {
        MimeMultipart mimeMultipart = send(client, in, response()
                .withStatusCode(200));
        assertThat(mimeMultipart.getCount()).isEqualTo(2);
        StandardBusinessDocument standardBusinessDocument = assertThatStandardBusinessDocumentIsCorrect(mimeMultipart.getBodyPart(0), out);
        assertThatParcelIsCorrect(standardBusinessDocument, mimeMultipart.getBodyPart(1));
    }

    @SneakyThrows
    private StandardBusinessDocument assertThatStandardBusinessDocumentIsCorrect(BodyPart sbdPart, Resource expectedSBD) {
        assertThat(sbdPart.getContentType()).isEqualTo("application/jwt");
        assertThat(sbdPart.getFileName()).isEqualTo("sbd.jwt");

        SharedByteArrayInputStream content = (SharedByteArrayInputStream) sbdPart.getContent();
        JWSObject jwsObject = JWSObject.parse(IOUtils.toString(content, StandardCharsets.UTF_8));
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) getSigningCertificate(jwsObject).getPublicKey());
        assertThat(jwsObject.verify(verifier)).isTrue();

        Payload payload = jwsObject.getPayload();

        StandardBusinessDocument standardBusinessDocument = dpiMapper.readStandardBusinessDocument(payload.toString());

        String type = StandardBusinessDocumentUtils.getType(standardBusinessDocument).orElse(null);
        jsonDigitalPostSchemaValidator.validate(payload.toJSONObject(), type);

        assertThatJson(payload.toString())
                .when(paths(String.format("standardBusinessDocument.%s.dokumentpakkefingeravtrykk.digestValue", type)), then(Option.IGNORING_VALUES))
                .isEqualTo(IOUtils.toString(expectedSBD.getInputStream(), StandardCharsets.UTF_8));

        return standardBusinessDocument;
    }

    @SneakyThrows
    private X509Certificate getSigningCertificate(JWSObject jwsObject) {
        return X509CertChainUtils.parse(jwsObject.getHeader().getX509CertChain())
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Can not find signing certificate!"));
    }

    private MimeMultipart send(MockServerClient client, Resource in, HttpResponse httpResponse) throws MessagingException {
        HttpRequest requestDefinition = request()
                .withMethod("POST")
                .withPath("/dpi/send");

        client.when(requestDefinition)
                .respond(httpResponse);

        Shipment shipment = shipmentFactory.getShipment(DpiTestInput.builder()
                .standardBusinessDocument(in)
                .mainDocument(hoveddokument)
                .attachments(Collections.singletonList(vedlegg))
                .mailbox("dummy")
                .receiverCertificate(sertifikat)
                .build()
        );

        dpiClient.send(shipment);

        client.verify(request()
                .withMethod("POST")
                .withPath("/token"));

        client.verify(requestDefinition);

        HttpRequest httpRequest = getRecordedRequest(client, requestDefinition);
        return getMimeMultipart(httpRequest);
    }

    @SneakyThrows
    private void assertThatParcelIsCorrect(StandardBusinessDocument standardBusinessDocument, BodyPart sbdPart) {
        assertThat(sbdPart.getContentType()).isEqualTo("application/cms");
        assertThat(sbdPart.getFileName()).isEqualTo("asic.cms");

        SharedByteArrayInputStream content = (SharedByteArrayInputStream) sbdPart.getContent();
        InputStream asicInputStream = decryptCMSDocument.decrypt(content);
        Parcel parcel = parcelParser.parse(
                standardBusinessDocument.getStandardBusinessDocumentHeader().getDocumentIdentification().getInstanceIdentifier(),
                asicInputStream);

        assertThat(parcel.getMainDocument().getFilename()).isEqualTo("svada.pdf");
        assertThat(parcel.getMainDocument().getMimeType()).isEqualTo("application/pdf");
        assertThat(ResourceUtils.toByteArray(parcel.getMainDocument().getResource()))
                .isEqualTo(ResourceUtils.toByteArray(hoveddokument));
        assertThat(parcel.getAttachments()).hasSize(1);
        assertThat(parcel.getAttachments().get(0).getFilename()).isEqualTo("bilde.png");
        assertThat(parcel.getAttachments().get(0).getMimeType()).isEqualTo("image/png");
        assertThat(ResourceUtils.toByteArray(parcel.getAttachments().get(0).getResource()))
                .isEqualTo(ResourceUtils.toByteArray(vedlegg));
    }

    private MimeMultipart getMimeMultipart(HttpRequest httpRequest) throws MessagingException {
        String contentTypeValue = httpRequest.getFirstHeader(HttpHeaders.CONTENT_TYPE);
        ContentType contentType = ContentType.parse(contentTypeValue);
        return new MimeMultipart(new ByteArrayDataSource(httpRequest.getBodyAsRawBytes(), contentType.getMimeType()));
    }

    private HttpRequest getRecordedRequest(MockServerClient client, HttpRequest requestDefinition) {
        RequestDefinition[] recordedRequests = client.retrieveRecordedRequests(requestDefinition);
        assertThat(recordedRequests).hasSize(1);
        RequestDefinition recordedRequest = recordedRequests[0];
        assertThat(recordedRequest).isInstanceOf(HttpRequest.class);
        return (HttpRequest) recordedRequest;
    }
}