package no.digdir.dpi.client;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.SchemaStore;
import net.jimblackler.jsonschemafriend.Validator;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.move.common.oauth.JwtTokenClient;
import no.difi.move.common.oauth.JwtTokenConfig;
import no.digdir.dpi.client.domain.KeyPair;
import no.digdir.dpi.client.internal.*;
import no.digipost.api.xml.Schemas;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.security.Security;

@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = "no.digdir.dpi.client")
@RequiredArgsConstructor
public class DpiClientConfig {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final DpiClientProperties properties;

    @Bean
    public DpiClient dpiClient(CreateCmsEncryptedAsice createCmsEncryptedAsice,
                               CreateMaskinportenToken createMaskinportenToken,
                               CreateParcelFingerprint createParcelFingerprint,
                               StandBusinessDocumentJsonFinalizer standBusinessDocumentJsonFinalizer,
                               CreateJWT createJWT,
                               CreateMultipart createMultipart) {
        return new DpiClient(
                createCmsEncryptedAsice,
                createMaskinportenToken,
                createParcelFingerprint,
                standBusinessDocumentJsonFinalizer,
                createJWT,
                createMultipart,
                WebClient.builder()
                        .baseUrl(properties.getUri())
                        .build());
    }

    @Bean
    public JwtTokenClient jwtTokenClient() {
        return new JwtTokenClient(new JwtTokenConfig(
                properties.getOidc().getClientId(),
                properties.getOidc().getUrl().toString(),
                properties.getOidc().getAudience(),
                properties.getOidc().getScopes(),
                properties.getOidc().getKeystore()
        ));
    }

    @Bean
    public CreateJWT createJWT(KeyPair keyPair) {
        return new CreateJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
                new RSASSASigner(keyPair.getBusinessCertificatePrivateKey()));
    }

    @Bean
    public CreateCMSDocument createCMSDocument(AlgorithmIdentifier rsaesOaepIdentifier) {
        return new CreateCMSDocument(CMSAlgorithm.AES256_CBC, rsaesOaepIdentifier);
    }

    @Bean
    public AlgorithmIdentifier rsaesOaepIdentifier() {
        AlgorithmIdentifier hash = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE);
        AlgorithmIdentifier mask = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, hash);
        AlgorithmIdentifier pSource = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(new byte[0]));
        ASN1Encodable parameters = new RSAESOAEPparams(hash, mask, pSource);
        return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, parameters);
    }

    @Bean
    @SneakyThrows
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(SDPManifest.class);
        marshaller.setSchema(Schemas.SDP_MANIFEST_SCHEMA);
        marshaller.afterPropertiesSet();
        return marshaller;
    }

    @Bean
    public InMemoryWithTempFileFallbackResourceFactory inMemoryWithTempFileFallbackResourceFactory() {
        return InMemoryWithTempFileFallbackResourceFactory.builder()
                .threshold(properties.getTemporaryFileThreshold())
                .directory(properties.getTemporaryFileDirectory())
                .initialBufferSize(properties.getInitialBufferSize())
                .build();
    }

    @Bean
    public JsonDigitalPostSchemaValidator jsonDigitalPostSchemaValidator() throws GenerationException {
        SchemaStore schemaStore = new SchemaStore();
        Schema schema = schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/digitalpost.schema.json"));
        return new JsonDigitalPostSchemaValidator(new Validator(), schema);
    }
}
