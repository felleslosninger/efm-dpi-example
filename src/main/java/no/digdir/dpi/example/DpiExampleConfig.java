package no.digdir.dpi.example;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.difi.move.common.cert.KeystoreProvider;
import no.difi.move.common.cert.KeystoreProviderException;
import no.difi.move.common.config.KeystoreProperties;
import no.digdir.dpi.client.DpiClientConfig;
import no.digdir.dpi.client.domain.Noekkelpar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.security.KeyStore;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Import(DpiClientConfig.class)
public class DpiExampleConfig {

    private final DpiExampleProperties properties;
    private final TrustedCertificates trustedCertificates;

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Europe/Oslo"));
    }

    @Bean
    public Noekkelpar noekkelpar() throws KeystoreProviderException {
        KeyStore keyStore = KeystoreProvider.loadKeyStore(properties.getDpi().getKeystore());

        return new Noekkelpar()
                .setKeyStore(keyStore)
                .setTrustStore(getTrustStore()
                        .map(this::addTrustedCertificates)
                        .orElseGet(trustedCertificates::getTrustStore))
                .setVirksomhetssertifikatAlias(properties.getDpi().getKeystore().getAlias())
                .setVirksomhetssertifikatPassword(properties.getDpi().getKeystore().getPassword());
    }

    private Optional<KeyStore> getTrustStore() {
        return Optional.ofNullable(properties.getDpi().getTrustStore())
                .map(this::loadTrustStore);
    }

    private KeyStore addTrustedCertificates(KeyStore trustStore) {
        KeyStore trustedSDP = trustedCertificates.getTrustStore();
        Enumeration<String> aliases = getAliases(trustedSDP);
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            setCertificateEntry(trustStore, trustedSDP, alias);
        }
        return trustStore;
    }

    @SneakyThrows
    private void setCertificateEntry(KeyStore trustStore, KeyStore trustedSDP, String alias) {
        trustStore.setCertificateEntry(alias, trustedSDP.getCertificate(alias));
    }

    @SneakyThrows
    private Enumeration<String> getAliases(KeyStore trustedSDP) {
        return trustedSDP.aliases();
    }

    @SneakyThrows
    private KeyStore loadTrustStore(KeystoreProperties properties) {
        return KeystoreProvider.loadKeyStore(properties);
    }
}