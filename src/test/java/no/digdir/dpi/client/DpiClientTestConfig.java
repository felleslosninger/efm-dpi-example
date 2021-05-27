package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.cert.KeystoreHelper;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@RequiredArgsConstructor
public class DpiClientTestConfig {

    private final DpiServerProperties properties;

    @Bean
    public KeystoreHelper serverKeystoreHelper() {
        return new KeystoreHelper(properties.getKeystore());
    }

    @Bean
    public DecryptCMSDocument decryptCMSDocument(JceKeyTransRecipient jceKeyTransRecipient) {
        return new DecryptCMSDocument(jceKeyTransRecipient);
    }

    @Bean
    public JceKeyTransRecipient jceKeyTransRecipient(KeystoreHelper serverKeystoreHelper) {
        JceKeyTransRecipient recipient = new JceKeyTransEnvelopedRecipient(serverKeystoreHelper.loadPrivateKey());
        return serverKeystoreHelper.shouldLockProvider() ? recipient.setProvider(serverKeystoreHelper.getKeyStore().getProvider()) : recipient;
    }

    @Bean
    public AsicParser asicParser() {
        return new AsicParser();
    }

//    @Bean
//    public DokumentpakkeParser dokumentpakkeParser(AsicParser asicParser) {
//        return new DokumentpakkeParser(asicParser);
//    }
}
