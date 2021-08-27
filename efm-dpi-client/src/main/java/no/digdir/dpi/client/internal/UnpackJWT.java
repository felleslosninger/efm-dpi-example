package no.digdir.dpi.client.internal;

import com.nimbusds.jose.proc.BadJWSException;
import lombok.RequiredArgsConstructor;
import no.difi.move.common.oauth.JWTDecoder;

import java.net.URL;

@RequiredArgsConstructor
public class UnpackJWT {

    private final JWTDecoder jwtDecoder;
    private final URL jwkUrl;

    public String getPayload(String serializedJwt) {
        try {
            return jwkUrl != null
                    ? jwtDecoder.getPayload(serializedJwt, jwkUrl)
                    : jwtDecoder.getPayload(serializedJwt);
        } catch (BadJWSException e) {
            throw new Exception("Couldn't get payload from serialized JWT", e);
        }
    }

    private static class Exception extends RuntimeException {

        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
