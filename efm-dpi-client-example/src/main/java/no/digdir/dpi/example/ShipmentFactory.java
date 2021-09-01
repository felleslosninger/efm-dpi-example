package no.digdir.dpi.example;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.difi.meldingsutveksling.domain.sbdh.ScopeType;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentUtils;
import no.digdir.dpi.client.FileExtensionMapper;
import no.digdir.dpi.client.domain.BusinessCertificate;
import no.digdir.dpi.client.domain.Document;
import no.digdir.dpi.client.domain.Parcel;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.messagetypes.BusinessMessage;
import no.digdir.dpi.client.internal.DpiMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShipmentFactory {

    private final FileExtensionMapper fileExtensionMapper;
    private final DpiMapper dpiMapper;

    public Shipment getShipment(DpiExampleInput input) {
        StandardBusinessDocument sbd = getStandardBusinessDocument(input);
        return new Shipment()
                .setSenderOrganizationIdentifier(StandardBusinessDocumentUtils.getFirstSenderIdentifier(sbd)
                        .orElseThrow(() -> new IllegalArgumentException("Missing sender identifier!"))
                )
                .setReceiverOrganizationIdentifier(StandardBusinessDocumentUtils.getFirstReceiverIdentifier(sbd)
                        .orElseThrow(() -> new IllegalArgumentException("Missing receiver identifier!"))
                )
                .setConversationId(StandardBusinessDocumentUtils.getScope(sbd, ScopeType.CONVERSATION_ID)
                        .flatMap(p -> Optional.ofNullable(p.getInstanceIdentifier()))
                        .orElseThrow(() -> new IllegalArgumentException("Missing conversationId!"))
                )
                .setExpectedResponseDateTime(StandardBusinessDocumentUtils.getExpectedResponseDateTime(sbd)
                        .orElse(null)
                )
                .setBusinessMessage(sbd.getBusinessMessage(BusinessMessage.class)
                        .orElseThrow(() -> new IllegalArgumentException("Missing business message")))
                .setParcel(getParcel(input))
                .setReceiverBusinessCertificate(getReceiverCertificate(input))
                .setLanguage("NO");
    }

    @SneakyThrows
    private StandardBusinessDocument getStandardBusinessDocument(DpiExampleInput input) {
        return dpiMapper.readStandardBusinessDocument(input.getStandardBusinessDocument());
    }

    private BusinessCertificate getReceiverCertificate(DpiExampleInput input) {
        try (InputStream inputStream = input.getReceiverCertificate().getInputStream()) {
            return BusinessCertificate.of(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new Exception("Couldn't get receiver certificate!", e);
        }
    }

    private Parcel getParcel(DpiExampleInput input) {
        return new Parcel()
                .setMainDocument(getDocument(input.getMainDocument()))
                .setAttachments(input.getAttachments()
                        .stream()
                        .map(this::getDocument)
                        .collect(Collectors.toList()));
    }

    private Document getDocument(Resource resource) {
        return new Document()
                .setTitle(resource.getDescription())
                .setFilename(resource.getFilename())
                .setResource(resource)
                .setMimeType(fileExtensionMapper.getMimetype(resource));
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
