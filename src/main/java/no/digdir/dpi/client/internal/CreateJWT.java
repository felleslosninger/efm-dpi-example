package no.digdir.dpi.client.internal;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;


@Slf4j
@RequiredArgsConstructor
public class CreateJWT {

    private final DpiMapper dpiMapper;
    private final JWSHeader jwsHeader;
    private final JWSSigner jwsSigner;

    @SneakyThrows
    public String createJWT(StandardBusinessDocument standardBusinessDocument) {
        Payload payload = new Payload(dpiMapper.writeStandardBusinessDocument(standardBusinessDocument));
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(jwsSigner);
        return jwsObject.serialize();
    }
}
