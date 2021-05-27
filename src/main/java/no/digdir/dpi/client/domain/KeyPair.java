package no.digdir.dpi.client.domain;

import lombok.Data;

import java.security.*;
import java.security.cert.Certificate;

@Data
public class KeyPair {

    KeyStore keyStore;
    KeyStore trustStore;
    String businessCertificateAlias;
    String businessCertificatePassword;

    public String getAlias() {
        return businessCertificateAlias;
    }

    public BusinessCertificate getBusinessCertificate() {
        return BusinessCertificate.fraKeyStore(keyStore, businessCertificateAlias);
    }

    public Certificate[] getBusinessCertificateChain() {
        try {
            return keyStore.getCertificateChain(businessCertificateAlias);
        } catch (KeyStoreException e) {
            throw new Exception("Kunne ikke hente privatnøkkel fra KeyStore. Er KeyStore initialisiert?", e);
        }
    }

    public PrivateKey getBusinessCertificatePrivateKey() {
        try {

            Key key = keyStore.getKey(businessCertificateAlias, businessCertificatePassword.toCharArray());
            if (!(key instanceof PrivateKey)) {
                throw new Exception("Kunne ikke hente privatnøkkel fra KeyStore. Forventet å få en PrivateKey, fikk " + key.getClass().getCanonicalName());
            }
            return (PrivateKey) key;
        } catch (KeyStoreException e) {
            throw new Exception("Kunne ikke hente privatnøkkel fra KeyStore. Er KeyStore initialisiert?", e);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("Kunne ikke hente privatnøkkel fra KeyStore. Verifiser at nøkkelen er støttet på plattformen", e);
        } catch (UnrecoverableKeyException e) {
            throw new Exception("Kunne ikke hente privatnøkkel fra KeyStore. Sjekk at passordet er riktig.", e);
        }
    }

    private static class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }

        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
