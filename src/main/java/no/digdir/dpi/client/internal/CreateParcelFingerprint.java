package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.sbd.Dokumentpakkefingeravtrykk;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class CreateParcelFingerprint {

    public Dokumentpakkefingeravtrykk createParcelFingerprint(CmsEncryptedAsice cmsEncryptedAsice) {
        try (InputStream inputStream = cmsEncryptedAsice.getResource().getInputStream()) {
            return new Dokumentpakkefingeravtrykk()
                    .setDigestMethod("http://www.w3.org/2001/04/xmlenc#sha256")
                    .setDigestValue(Base64.getEncoder().encodeToString(DigestUtils.sha256(inputStream)));
        } catch (IOException e) {
            throw new CreateParcelFingerprint.Exception("Failed to create parcel fingerprint", e);
        }
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
