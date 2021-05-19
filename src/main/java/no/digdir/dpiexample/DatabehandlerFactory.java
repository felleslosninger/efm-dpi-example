package no.digdir.dpiexample;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.cert.KeystoreProvider;
import no.difi.move.common.cert.KeystoreProviderException;
import no.difi.sdp.client2.domain.AktoerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.NoekkelparOverride;
import no.difi.sdp.client2.internal.TrustedCertificates;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

@Component
@RequiredArgsConstructor
public class DatabehandlerFactory {

    private final DpiExampleProperties props;

    public Databehandler getDatabehandler(AktoerOrganisasjonsnummer aktoerOrganisasjonsnummer) throws KeystoreProviderException, KeyStoreException {
        KeyStore keyStore = KeystoreProvider.loadKeyStore(props.getDpi().getKeystore());

        if (props.getDpi().getTrustStore() != null) {
            KeyStore trustStore = KeystoreProvider.loadKeyStore(props.getDpi().getTrustStore());
            KeyStore trustedSDP = TrustedCertificates.getTrustStore();
            Enumeration<String> aliases = trustedSDP.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                trustStore.setCertificateEntry(alias, trustedSDP.getCertificate(alias));
            }

            Noekkelpar noekkelparOverride = new NoekkelparOverride(keyStore, trustStore,
                    props.getDpi().getKeystore().getAlias(),
                    props.getDpi().getKeystore().getPassword(),
                    false);
            return Databehandler.builder(aktoerOrganisasjonsnummer.forfremTilDatabehandler(), noekkelparOverride).build();
        } else {
            return Databehandler.builder(aktoerOrganisasjonsnummer.forfremTilDatabehandler(),
                    Noekkelpar.fraKeyStoreUtenTrustStore(keyStore,
                            props.getDpi().getKeystore().getAlias(),
                            props.getDpi().getKeystore().getPassword()))
                    .build();
        }
    }
}
