package no.digdir.dpi.client.internal;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

public class TrustedCertificates {

    private static final List<String> TRUSTED_CERTIFICATES = Arrays.asList(
            "classpath:/certificates/prod/BPClass3RootCA.cer",
            "classpath:/certificates/prod/commfides_root_ca.cer",
            "classpath:/certificates/prod/BPClass3CA3.cer",
            "classpath:/certificates/prod/commfides_ca.cer",
            "classpath:/certificates/test/Buypass_Class_3_Test4_Root_CA.cer",
            "classpath:/certificates/test/commfides_test_root_ca.cer",
            "classpath:/certificates/test/Buypass_Class_3_Test4_CA_3.cer",
            "classpath:/certificates/test/commfides_test_ca.cer"
    );

    @Getter
    private final KeyStore trustStore;

    public TrustedCertificates(ResourceLoader resourceLoader) {
        this.trustStore = createTrustStore(resourceLoader);
    }

    public KeyStore createTrustStore(ResourceLoader resourceLoader) {
        KeyStore truststore = createEmptyTrustStore();
        TRUSTED_CERTIFICATES.stream()
                .map(resourceLoader::getResource)
                .map(this::readCertificate)
                .forEach(p -> addCertificateToTrustStore(p, truststore));
        return truststore;
    }

    private KeyStore createEmptyTrustStore() {
        try {
            KeyStore truststore = KeyStore.getInstance("JCEKS");
            truststore.load(null, "".toCharArray());
            return truststore;
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            throw new Exception("Creating empty keystore failed!", e);
        }
    }

    private X509Certificate readCertificate(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(inputStream);
        } catch (CertificateException | IOException e) {
            throw new Exception("Reading cerificate failed!", e);
        }
    }

    public void addCertificateToTrustStore(X509Certificate certificate, KeyStore trustStore) {
        try {
            trustStore.setCertificateEntry(getAlias(certificate), certificate);
        } catch (KeyStoreException e) {
            throw new Exception("Klarte ikke å legge til sertifikat til trust store.", e);
        }
    }

    private String getAlias(X509Certificate certificate) {
        return certificate.getSerialNumber().toString() + Math.random();
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
