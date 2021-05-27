package no.digdir.dpi.client.domain;

import lombok.Value;
import no.digdir.dpi.client.exception.SertifikatException;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.*;

@Value
public class Sertifikat {

    X509Certificate x509Certificate;

    public PublicKey getPublicKey() {
        return x509Certificate.getPublicKey();
    }

    public byte[] getEncoded() {
        try {
            return x509Certificate.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new SertifikatException("Kunne ikke hente encoded utgave av sertifikatet", e);
        }
    }

    public static Sertifikat fraByteArray(byte[] certificate) {
        try {
            return lagSertifikat(certificate);
        } catch (CertificateException e) {
            throw new SertifikatException("Kunne ikke lese sertifikat fra byte array", e);
        }
    }

    public static Sertifikat fraCertificate(X509Certificate certificate) {
        return new Sertifikat(certificate);
    }

    public static Sertifikat fraKeyStore(KeyStore keyStore, String alias) {
        Certificate certificate;
        try {
            certificate = keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new SertifikatException("Klarte ikke lese sertifikat fra keystore", e);
        }

        if (certificate == null) {
            throw new SertifikatException("Kunne ikke finne sertifikat i keystore. Er du sikker på at det er brukt keystore med et sertifikat og at du har oppgitt riktig alias?");
        }

        if (!(certificate instanceof X509Certificate)) {
            throw new SertifikatException("Klienten støtter kun X509-sertifikater. Fikk sertifikat av typen " + certificate.getClass().getSimpleName());
        }

        return new Sertifikat((X509Certificate) certificate);
    }

    private static Sertifikat lagSertifikat(byte[] certificate) throws CertificateException {
        X509Certificate x509Certificate = (X509Certificate) CertificateFactory
                .getInstance("X509")
                .generateCertificate(new ByteArrayInputStream(certificate));
        return new Sertifikat(x509Certificate);
    }
}
