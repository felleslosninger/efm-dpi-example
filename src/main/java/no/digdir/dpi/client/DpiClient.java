package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.Forsendelse;
import no.digdir.dpi.client.domain.SendResultat;
import no.digdir.dpi.client.domain.sbd.Dokumentpakkefingeravtrykk;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import no.digdir.dpi.client.internal.CreateDokumentpakke;
import no.digdir.dpi.client.internal.CreateJWT;
import no.digdir.dpi.client.internal.domain.Billable;
import no.digipost.api.representations.Dokumentpakke;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class DpiClient {

    private final Clock clock;
    private final CreateDokumentpakke createDokumentpakke;
    private final CreateJWT createJWT;

    @SneakyThrows
    public SendResultat send(Forsendelse forsendelse) {
        Billable<Dokumentpakke> billable = createDokumentpakke.createDokumentpakke(forsendelse);
        Dokumentpakke dokumentpakke = billable.getEntity();

        File asic = getAsic(dokumentpakke);

        StandardBusinessDocument standardBusinessDocument = forsendelse.getStandardBusinessDocument();

        standardBusinessDocument.getDigitalpost()
                .setDokumentpakkefingeravtrykk(getFingeravtrykk(dokumentpakke));

        String jwt = createJWT.createJWT(standardBusinessDocument);
        log.info("JWT: {}", jwt);

        return null;
//        return new SendResultat(entity.messageId, entity.refToMessageId, billable.billableBytes);
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

    private Dokumentpakkefingeravtrykk getFingeravtrykk(Dokumentpakke dokumentpakke) throws IOException {
        return new Dokumentpakkefingeravtrykk()
                .setDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256")
                .setDigestValue(Base64.getEncoder().encodeToString(dokumentpakke.getSHA256()));
    }
}
