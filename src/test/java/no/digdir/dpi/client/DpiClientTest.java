package no.digdir.dpi.client;


import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import lombok.SneakyThrows;
import no.digdir.dpi.client.domain.Parcel;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.KeyPair;
import no.digdir.dpi.example.DpiExampleConfig;
import no.digdir.dpi.example.DpiExampleInput;
import no.digdir.dpi.example.ForsendelseFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.RequestDefinition;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.mail.util.SharedByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {DpiExampleConfig.class, DpiClientTestConfig.class})
@WebAppConfiguration
@ActiveProfiles("itest")
@ExtendWith({SpringExtension.class, MockServerExtension.class})
@MockServerSettings(ports = 8900)
class DpiClientTest {

    @Autowired
    private DpiClient dpiClient;

    @Autowired
    private ForsendelseFactory forsendelseFactory;

    @Autowired
    private KeyPair keyPair;

    @Autowired
    private DecryptCMSDocument decryptCMSDocument;

    @Autowired
    private ParcelParser parcelParser;

    @Value("classpath:/digital.sbd")
    private Resource digitalSbd;

    @Value("classpath:/svada.pdf")
    private Resource hoveddokument;

    @Value("classpath:/bilde.png")
    private Resource vedlegg;

    @Value("classpath:/c2.cer")
    private Resource sertifikat;

    @Test
    @SneakyThrows
    void testSend(MockServerClient client) {
        HttpRequest requestDefinition = request()
                .withMethod("POST")
                .withPath("/dpi");

        client.when(requestDefinition)
                .respond(response()
                        .withStatusCode(200));

        Shipment shipment = forsendelseFactory.getForsendelse(DpiExampleInput.builder()
                .standardBusinessDocument(digitalSbd)
                .mainDocument(hoveddokument)
                .attachments(Collections.singletonList(vedlegg))
                .mailbox("dummy")
                .receiverCertificate(sertifikat)
                .build()
        );

        dpiClient.send(shipment);

        client.verify(requestDefinition);

        HttpRequest httpRequest = getRecordedRequest(client, requestDefinition);
        MimeMultipart mimeMultipart = getMimeMultipart(httpRequest);

        assertThat(mimeMultipart.getCount()).isEqualTo(2);
        assertThatStandardBusinessDocumentIsCorrect(mimeMultipart.getBodyPart(0));
        assertThatParcelIsCorrect(mimeMultipart.getBodyPart(1));
    }

    @SneakyThrows
    private void assertThatStandardBusinessDocumentIsCorrect(BodyPart sbdPart) {
        assertThat(sbdPart.getContentType()).isEqualTo("application/jwt");
        assertThat(sbdPart.getFileName()).isEqualTo("sbd.jwt");

        SharedByteArrayInputStream content = (SharedByteArrayInputStream) sbdPart.getContent();
        JWSObject jwsObject = JWSObject.parse(IOUtils.toString(content, StandardCharsets.UTF_8));
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) keyPair.getBusinessCertificate().getPublicKey());
        assertThat(jwsObject.verify(verifier)).isTrue();

        JSONAssert.assertNotEquals(
                IOUtils.toString(digitalSbd.getInputStream(), StandardCharsets.UTF_8),
                jwsObject.getPayload().toString(),
                JSONCompareMode.STRICT);
    }

    @SneakyThrows
    private void assertThatParcelIsCorrect(BodyPart sbdPart) {
        assertThat(sbdPart.getContentType()).isEqualTo("application/cms");
        assertThat(sbdPart.getFileName()).isEqualTo("asic.cms");

        SharedByteArrayInputStream content = (SharedByteArrayInputStream) sbdPart.getContent();
        InputStream asicInputStream = decryptCMSDocument.decrypt(content);
        Parcel parcel = parcelParser.parse(asicInputStream);

        assertThat(parcel.getMainDocument().getFilename()).isEqualTo("svada.pdf");
        assertThat(parcel.getMainDocument().getMimeType()).isEqualTo("application/pdf");
        assertThat(parcel.getMainDocument().getBytes()).isEqualTo(getBytes(hoveddokument));
        assertThat(parcel.getAttachments()).hasSize(1);
        assertThat(parcel.getAttachments().get(0).getFilename()).isEqualTo("bilde.png");
        assertThat(parcel.getAttachments().get(0).getMimeType()).isEqualTo("image/png");
        assertThat(parcel.getAttachments().get(0).getBytes()).isEqualTo(getBytes(vedlegg));
    }

    @SneakyThrows
    private byte[] getBytes(Resource resource) {
        return IOUtils.toByteArray(resource.getInputStream());
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