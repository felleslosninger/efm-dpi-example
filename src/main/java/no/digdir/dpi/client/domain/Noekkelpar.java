package no.digdir.dpi.client.domain;

import lombok.Data;
import no.digdir.dpi.client.exception.NoekkelException;

import java.security.*;
import java.security.cert.Certificate;

@Data
public class Noekkelpar {

    KeyStore keyStore;
    KeyStore trustStore;
    String virksomhetssertifikatAlias;
    String virksomhetssertifikatPassword;

    public String getAlias() {
        return virksomhetssertifikatAlias;
    }

    public Sertifikat getVirksomhetssertifikat() {
        return Sertifikat.fraKeyStore(keyStore, virksomhetssertifikatAlias);
    }

    public Certificate[] getVirksomhetssertifikatKjede() {
        try {
            return keyStore.getCertificateChain(virksomhetssertifikatAlias);
        } catch (KeyStoreException e) {
            throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Er KeyStore initialisiert?", e);
        }
    }

    public PrivateKey getVirksomhetssertifikatPrivatnoekkel() {
        try {
            Key key = keyStore.getKey(virksomhetssertifikatAlias, virksomhetssertifikatPassword.toCharArray());
            if (!(key instanceof PrivateKey)) {
                throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Forventet å få en PrivateKey, fikk " + key.getClass().getCanonicalName());
            }
            return (PrivateKey) key;
        } catch (KeyStoreException e) {
            throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Er KeyStore initialisiert?", e);
        } catch (NoSuchAlgorithmException e) {
            throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Verifiser at nøkkelen er støttet på plattformen", e);
        } catch (UnrecoverableKeyException e) {
            throw new NoekkelException("Kunne ikke hente privatnøkkel fra KeyStore. Sjekk at passordet er riktig.", e);
        }
    }
}
