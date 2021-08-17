package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.BusinessCertificate;
import no.digdir.dpi.client.domain.Document;
import no.digdir.dpi.client.domain.Parcel;
import no.digdir.dpi.client.domain.Shipment;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShipmentFactory {

    private final FileExtensionMapper fileExtensionMapper;

    public Shipment getShipment(DpiTestInput input) {
        return new Shipment()
                .setSenderOrganizationIdentifier(input.getSenderOrganizationIdentifier())
                .setReceiverOrganizationIdentifier(input.getReceiverOrganizationIdentifier())
                .setConversationId(input.getConversationId())
                .setExpectedResponseDateTime(input.getExpectedResponseDateTime())
                .setBusinessMessage(input.getBusinessMessage())
                .setParcel(getParcel(input))
                .setReceiverBusinessCertificate(getReceiverCertificate(input))
                .setLanguage("NO");
    }

    private BusinessCertificate getReceiverCertificate(DpiTestInput input) {
        try (InputStream inputStream = input.getReceiverCertificate().getInputStream()) {
            return BusinessCertificate.fraByteArray(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new Exception("Couldn't get receiver certificate!", e);
        }
    }

    private Parcel getParcel(DpiTestInput input) {
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
