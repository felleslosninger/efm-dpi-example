package no.digdir.dpi.client;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import lombok.RequiredArgsConstructor;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.digdir.dpi.client.domain.KeyPair;
import no.digdir.dpi.client.exception.KonfigurasjonException;
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
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Security;
import java.time.Clock;

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
    public DpiClient dpiClient(Clock clock,
                               CreateDokumentpakke createDokumentpakke,
                               CreateMaskinportenToken createMaskinportenToken,
                               CreateJWT createJWT,
                               CreateMultipart createMultipart) {
        return new DpiClient(clock,
                createDokumentpakke,
                createMaskinportenToken,
                createJWT,
                createMultipart,
                WebClient.builder()
                        .baseUrl(properties.getUri())
                        .build(),
                new DefaultDataBufferFactory());
    }

    @Bean
    public CreateJWT createJWT(DpiMapper dpiMapper, KeyPair keyPair) {
        return new CreateJWT(
                dpiMapper,
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
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(SDPManifest.class);
        marshaller.setSchema(Schemas.SDP_MANIFEST_SCHEMA);
        try {
            marshaller.afterPropertiesSet();
        } catch (Exception e) {
            throw new KonfigurasjonException("Kunne ikke sette opp Jaxb marshaller", e);
        }
        return marshaller;
    }
}
