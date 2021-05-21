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
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ForsendelseFactory {

    private final FileExtensionMapper fileExtensionMapper;
    private final DpiMapper dpiMapper;

    public Forsendelse getForsendelse(DpiExampleInput input) throws IOException {
        return new Forsendelse()
                .setStandardBusinessDocument(getStandardBusinessDocument(input))
                .setDokumentpakke(getDokumentpakke(input))
                .setPostkasseadresse(input.getPostkasseadresse())
                .setMottakerSertifikat(getMottakerSertifikat(input))
                .setSpraakkode("NO");
    }

    @SneakyThrows
    private StandardBusinessDocument getStandardBusinessDocument(DpiExampleInput input) {
        return dpiMapper.readStandardBusinessDocument(input.getFiles().getStandardBusinessDocument());
    }

    private Sertifikat getMottakerSertifikat(DpiExampleInput input) throws IOException {
        return Sertifikat.fraByteArray(Files.readAllBytes(Paths.get(input.getSertifikat())));
    }

    private Dokumentpakke getDokumentpakke(DpiExampleInput input) {
        return new Dokumentpakke()
                .setHoveddokument(getDocument(input.getFiles().getHoveddokument()))
                .setVedlegg(input.getFiles().getVedlegg()
                        .stream()
                        .map(this::getDocument)
                        .collect(Collectors.toList()));
    }

    private Dokument getDocument(File file) {
        return new Dokument()
                .setTittel(file.getName())
                .setFilnavn(file.getName())
                .setDokument(getDokument(file))
                .setMimeType(fileExtensionMapper.getMimetype(file));
    }

    private byte[] getDokument(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
