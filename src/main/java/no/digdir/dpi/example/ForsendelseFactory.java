package no.digdir.dpi.example;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.digdir.dpi.client.domain.Dokument;
import no.digdir.dpi.client.domain.Dokumentpakke;
import no.digdir.dpi.client.domain.Forsendelse;
import no.digdir.dpi.client.domain.Sertifikat;
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

    public Forsendelse getForsendelse(DpiExampleInput input) {
        return new Forsendelse()
                .setStandardBusinessDocument(getStandardBusinessDocument(input))
                .setDokumentpakke(getDokumentpakke(input))
                .setPostkasseadresse(input.getPostkasseadresse())
                .setMottakerSertifikat(getMottakerSertifikat(input))
                .setSpraakkode("NO");
    }

    @SneakyThrows
    private StandardBusinessDocument getStandardBusinessDocument(DpiExampleInput input) {
        return dpiMapper.readStandardBusinessDocument(input.getStandardBusinessDocument());
    }

    private Sertifikat getMottakerSertifikat(DpiExampleInput input) {
        try (InputStream inputStream = input.getSertifikat().getInputStream()) {
            return Sertifikat.fraByteArray(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private Dokumentpakke getDokumentpakke(DpiExampleInput input) {
        return new Dokumentpakke()
                .setHoveddokument(getDocument(input.getHoveddokument()))
                .setVedlegg(input.getVedlegg()
                        .stream()
                        .map(this::getDocument)
                        .collect(Collectors.toList()));
    }

    private Dokument getDocument(Resource resource) {
        return new Dokument()
                .setTittel(resource.getDescription())
                .setFilnavn(resource.getFilename())
                .setDokument(getDokument(resource))
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
