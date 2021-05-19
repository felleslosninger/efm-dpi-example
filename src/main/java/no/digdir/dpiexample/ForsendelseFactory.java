package no.digdir.dpiexample;

import lombok.RequiredArgsConstructor;
import no.difi.sdp.client2.domain.*;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.digipost.api.representations.Organisasjonsnummer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ForsendelseFactory {

    private final FileExtensionMapper fileExtensionMapper;

    public Forsendelse getForsendelse(DpiExampleInput input) throws IOException {
        return Forsendelse.digital(
                getAvsender(input),
                getDigitalPost(input),
                getDokumentpakke(input)).build();
    }

    private Dokumentpakke getDokumentpakke(DpiExampleInput input) {
        Assert.isTrue(!input.getFiles().isEmpty(), "No files specified!");

        List<Dokument> dokumentList = input.getFiles()
                .stream()
                .map(this::getDocument)
                .collect(Collectors.toList());

        return Dokumentpakke.builder(dokumentList.get(0))
                .vedlegg(dokumentList.size() > 1 ? dokumentList.subList(1, dokumentList.size()) : Collections.emptyList())
                .build();
    }

    private Dokument getDocument(String path) {
        File file = new File(path);
        return Dokument.builder(file.getName(), file)
                .mimeType(fileExtensionMapper.getMimetype(file))
                .build();
    }

    private DigitalPost getDigitalPost(DpiExampleInput input) throws IOException {
        return DigitalPost.builder(getMottaker(input), input.getTittel()).build();
    }

    private Mottaker getMottaker(DpiExampleInput input) throws IOException {
        return Mottaker.builder(
                input.getMottaker(),
                input.getPostkasseadresse(),
                Sertifikat.fraByteArray(Files.readAllBytes(Paths.get(input.getSertifikat()))),
                Organisasjonsnummer.of(input.getOrgnrPostkasse())
        ).build();
    }

    private Avsender getAvsender(DpiExampleInput input) {
        return Avsender.builder(AktoerOrganisasjonsnummer.of(input.getAvsender()).forfremTilAvsender()).build();
    }
}
