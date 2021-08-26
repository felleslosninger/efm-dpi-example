package no.digdir.dpi.client.internal;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.X509CertChainUtils;
import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.Blame;
import no.digdir.dpi.client.DpiException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

@Component
@RequiredArgsConstructor
public class UnpackJWT {

    public Payload getPayload(String jwt) {
        JWSObject jwsObject = getJwsObject(jwt);
        verify(jwsObject);
        return jwsObject.getPayload();
    }

    private void verify(JWSObject jwsObject) {
        try {
            Assert.state(jwsObject.verify(getVerifier(jwsObject)), "Verifying JWT failed!");
        } catch (IllegalStateException | JOSEException e) {
            throw new UnpackJWT.Exception("Verifying JWT failed!", e);
        }
    }

    private RSASSAVerifier getVerifier(JWSObject jwsObject) {
        return new RSASSAVerifier((RSAPublicKey) getSigningCertificate(jwsObject).getPublicKey());
    }

    private JWSObject getJwsObject(String jwt) {
        try {
            return JWSObject.parse(jwt);
        } catch (ParseException e) {
            throw new UnpackJWT.Exception("Parsing JWT failed!", e);
        }
    }

    private X509Certificate getSigningCertificate(JWSObject jwsObject) {
        try {
            return X509CertChainUtils.parse(jwsObject.getHeader().getX509CertChain())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new DpiException("Can not find signing certificate!", Blame.SERVER));
        } catch (ParseException e) {
            throw new UnpackJWT.Exception("Can parse signing certificate!", e);
        }
    }

    private static class Exception extends RuntimeException {

        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}