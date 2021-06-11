package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.SchemaStore;
import net.jimblackler.jsonschemafriend.Validator;
import no.difi.move.common.cert.KeystoreHelper;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;

@TestConfiguration
@RequiredArgsConstructor
public class DpiClientTestConfig {

    private final DpiServerProperties properties;

    @Bean
    @Primary
    public Clock fixedClock() {
        return Clock.fixed(Instant.parse("2021-05-21T11:19:57.12Z"), ZoneId.of("Europe/Oslo"));
    }

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

    @Bean
    public InMemoryDocumentStorage inMemoryDocumentStorage() {
        return new InMemoryDocumentStorage();
    }

    @Bean
    public JsonDigitalPostSchemaValidator jsonDigitalPostSchemaValidator() throws GenerationException {
        SchemaStore schemaStore = new SchemaStore();
        Schema schema = schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/digitalpost.schema.json"));
        return new JsonDigitalPostSchemaValidator(new Validator(), schema);
    }
}
