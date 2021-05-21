package no.digdir.dpi.client.internal;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.Noekkelpar;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class CreateJWT {

    private final Noekkelpar noekkelpar;
    private final DpiMapper dpiMapper;

    public String createJWT(StandardBusinessDocument standardBusinessDocument) {
        String payload = dpiMapper.writeStandardBusinessDocument(standardBusinessDocument);
        log.info("payload: {}", payload);
        return Jwts.builder()
                .setPayload(payload)
                .signWith(noekkelpar.getVirksomhetssertifikatPrivatnoekkel())
                .compact();
    }
}
