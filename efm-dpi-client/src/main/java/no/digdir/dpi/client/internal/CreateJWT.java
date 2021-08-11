package no.digdir.dpi.client.internal;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class CreateJWT {

    private final JWSHeader jwsHeader;
    private final JWSSigner jwsSigner;

    @SneakyThrows
    public String createJWT(Map<String, Object> sbd) {
        Payload payload = new Payload(new JSONObject(sbd));
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(jwsSigner);
        return jwsObject.serialize();
    }
}
