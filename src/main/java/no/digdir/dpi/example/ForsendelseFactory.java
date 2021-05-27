package no.digdir.dpi.example;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.digdir.dpi.client.domain.BusinessCertificate;
import no.digdir.dpi.client.domain.Document;
import no.digdir.dpi.client.domain.Parcel;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import no.digdir.dpi.client.exception.RuntimeIOException;
import no.digdir.dpi.client.internal.DpiMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ForsendelseFactory {

    private final FileExtensionMapper fileExtensionMapper;
    private final DpiMapper dpiMapper;

    public Shipment getForsendelse(DpiExampleInput input) {
        return new Shipment()
                .setStandardBusinessDocument(getStandardBusinessDocument(input))
                .setParcel(getParcel(input))
                .setMailbox(input.getMailbox())
                .setReceiverBusinessCertificate(getReceiverCertificate(input))
                .setLanguage("NO");
    }

    @SneakyThrows
    private StandardBusinessDocument getStandardBusinessDocument(DpiExampleInput input) {
        return dpiMapper.readStandardBusinessDocument(input.getStandardBusinessDocument());
    }

    private BusinessCertificate getReceiverCertificate(DpiExampleInput input) {
        try (InputStream inputStream = input.getReceiverCertificate().getInputStream()) {
            return BusinessCertificate.fraByteArray(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
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
                .setBytes(getDokument(resource))
                .setMimeType(fileExtensionMapper.getMimetype(resource));
    }

    private byte[] getDokument(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
