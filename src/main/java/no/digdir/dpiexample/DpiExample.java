package no.digdir.dpiexample;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.difi.move.common.cert.KeystoreProviderException;
import no.difi.sdp.client2.domain.AktoerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.internal.CreateDokumentpakke;
import no.digipost.api.representations.Dokumentpakke;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyStoreException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class DpiExample {

    private final Clock clock;
    private final DpiExampleProperties properties;
    private final DatabehandlerFactory databehandlerFactory;
    private final ForsendelseFactory forsendelseFactory;
    private final CreateDokumentpakke createDokumentpakke;

    @SneakyThrows
    public DpiExampleOutput run(DpiExampleInput input) {
        Dokumentpakke dokumentpakke = createDokumentpakke.createDokumentpakke(
                getDatabehandler(),
                forsendelseFactory.getForsendelse(input)
        ).entity;

        return DpiExampleOutput.builder()
                .asic(getAsic(dokumentpakke))
                .fingeravtrykk(getFingeravtrykk(dokumentpakke))
                .build();
    }

    private File getAsic(Dokumentpakke dokumentpakke) throws IOException {
        File asic = new File(String.format("%s.%s",
                DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss").format(LocalDateTime.now(clock)),
                dokumentpakke.getName()
        ));

        log.info("Writing ASiC-E: {}", asic.getAbsolutePath());

        try (InputStream inputStream = dokumentpakke.getInputStream()) {
            Files.copy(
                    inputStream,
                    asic.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return asic;
    }

    private String getFingeravtrykk(Dokumentpakke dokumentpakke) throws IOException {
        return Base64.getEncoder().encodeToString(dokumentpakke.getSHA256());
    }

    private Databehandler getDatabehandler() throws KeystoreProviderException, KeyStoreException {
        return databehandlerFactory.getDatabehandler(AktoerOrganisasjonsnummer.of(properties.getOrg().getIdentifier()));
    }
}