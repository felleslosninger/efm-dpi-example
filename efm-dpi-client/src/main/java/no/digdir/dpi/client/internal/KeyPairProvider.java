package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.cert.KeystoreProvider;
import no.difi.move.common.cert.KeystoreProviderException;
import no.difi.move.common.config.KeystoreProperties;
import no.digdir.dpi.client.domain.BusinessCertificate;
import no.digdir.dpi.client.domain.KeyPair;

import java.security.*;
import java.security.cert.Certificate;

@RequiredArgsConstructor
public class KeyPairProvider {

    private final BusinessCertificateValidator businessCertificateValidator;
    private final KeystoreProperties properties;

    public KeyPair getKeyPair() {
        KeyStore keyStore = getKeyStore();

        return KeyPair.builder()
                .alias(properties.getAlias())
                .businessCertificate(getValidatedBusinessCertificate(keyStore, businessCertificateValidator))
                .businessCertificateChain(getBusinessCertificateChain(keyStore))
                .businessCertificatePrivateKey(getBusinessCertificatePrivateKey(keyStore))
                .build();
    }

    private KeyStore getKeyStore() {
        try {
            return KeystoreProvider.loadKeyStore(properties);
        } catch (KeystoreProviderException e) {
            throw new Exception("Couldn't load keystore!", e);
        }
    }

    private BusinessCertificate getValidatedBusinessCertificate(KeyStore keyStore, BusinessCertificateValidator businessCertificateValidator) {
        BusinessCertificate certificate = getBusinessCertificate(keyStore);
        businessCertificateValidator.validate(certificate.getX509Certificate());
        return certificate;
    }

    private BusinessCertificate getBusinessCertificate(KeyStore keyStore) {
        return BusinessCertificate.getFromKeyStore(keyStore, properties.getAlias());
    }

    private Certificate[] getBusinessCertificateChain(KeyStore keyStore) {
        try {
            return keyStore.getCertificateChain(properties.getAlias());
        } catch (KeyStoreException e) {
            throw new Exception("Kunne ikke hente sertifikatkjede fra KeyStore. Er KeyStore initialisiert?", e);
        }
    }

    public PrivateKey getBusinessCertificatePrivateKey(KeyStore keyStore) {
        try {

            Key key = keyStore.getKey(properties.getAlias(), properties.getPassword().toCharArray());
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
