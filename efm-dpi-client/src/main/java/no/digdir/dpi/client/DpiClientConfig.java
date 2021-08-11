package no.digdir.dpi.client;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.SchemaStore;
import net.jimblackler.jsonschemafriend.UrlRewriter;
import net.jimblackler.jsonschemafriend.Validator;
import no.difi.move.common.cert.KeystoreProvider;
import no.difi.move.common.cert.KeystoreProviderException;
import no.difi.move.common.config.KeystoreProperties;
import no.difi.move.common.oauth.JwtTokenClient;
import no.difi.move.common.oauth.JwtTokenConfig;
import no.digdir.dpi.client.domain.KeyPair;
import no.digdir.dpi.client.domain.messagetypes.MessageType;
import no.digdir.dpi.client.internal.*;
import no.digdir.dpi.client.internal.domain.CreateStandardBusinessDocumentJWT;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.net.URI;
import java.security.KeyStore;
import java.security.Security;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = "no.digdir.dpi.client")
@RequiredArgsConstructor
public class DpiClientConfig {

    private static final String LOG_MESSAGE = "Response {}: {}";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final DpiClientProperties properties;

    @Bean
    public DpiClient dpiClient(CreateCmsEncryptedAsice createCmsEncryptedAsice,
                               CreateMaskinportenToken createMaskinportenToken,
                               CreateStandardBusinessDocumentJWT createStandardBusinessDocumentJWT,
                               CreateMultipart createMultipart,
                               DpiClientErrorHandler dpiClientErrorHandler,
                               InMemoryWithTempFileFallbackResourceFactory resourceFactory,
                               MessageUnwrapper messageUnwrapper) {
        return new DpiClientImpl(
                createCmsEncryptedAsice,
                createMaskinportenToken,
                createStandardBusinessDocumentJWT,
                createMultipart,
                WebClient.builder()
                        .baseUrl(properties.getUri())
                        .filter(logRequest())
                        .filter(logResponse())
                        .clientConnector(new ReactorClientHttpConnector(HttpClient.from(TcpClient
                                .create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getTimeout().getConnect())
                                .doOnConnected(connection -> {
                                    connection.addHandlerLast(new ReadTimeoutHandler(properties.getTimeout().getRead(), TimeUnit.MILLISECONDS));
                                    connection.addHandlerLast(new WriteTimeoutHandler(properties.getTimeout().getWrite(), TimeUnit.MILLISECONDS));
                                }))))
                        .build(),
                dpiClientErrorHandler,
                resourceFactory,
                messageUnwrapper);
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Request {}: {} {}", clientRequest.logPrefix(), clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            switch (clientResponse.statusCode().series()) {
                case SUCCESSFUL:
                    log.debug(LOG_MESSAGE, clientResponse.logPrefix(), clientResponse.statusCode());
                    break;
                case INFORMATIONAL:
                case CLIENT_ERROR:
                    log.info(LOG_MESSAGE, clientResponse.logPrefix(), clientResponse.statusCode());
                    break;
                default:
                    log.warn(LOG_MESSAGE, clientResponse.logPrefix(), clientResponse.statusCode());
                    break;
            }

            return Mono.just(clientResponse);
        });
    }

    @Bean
    public DpiClientErrorHandler dpiClientErrorHandler() {
        return new DpiClientErrorHandlerImpl();
    }

    @Bean
    public CreateMaskinportenToken createMaskinportenToken() {
        return new CreateMaskinportenToken(jwtTokenClient());
    }

    private JwtTokenClient jwtTokenClient() {
        return new JwtTokenClient(new JwtTokenConfig(
                properties.getOidc().getClientId(),
                properties.getOidc().getUrl().toString(),
                properties.getOidc().getAudience(),
                properties.getOidc().getScopes(),
                properties.getOidc().getKeystore()
        ));
    }

    @Bean
    @SneakyThrows
    public CreateJWT createJWT(KeyPair keyPair) {
        return new CreateJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .x509CertChain(Collections.singletonList(Base64.encode(keyPair.getBusinessCertificate().getX509Certificate().getEncoded())))
                        .build(),
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
    public InMemoryWithTempFileFallbackResourceFactory inMemoryWithTempFileFallbackResourceFactory() {
        return InMemoryWithTempFileFallbackResourceFactory.builder()
                .threshold(properties.getTemporaryFileThreshold())
                .directory(properties.getTemporaryFileDirectory())
                .initialBufferSize(properties.getInitialBufferSize())
                .build();
    }

    @Bean
    public JsonDigitalPostSchemaValidator jsonDigitalPostSchemaValidator() {
        return new JsonDigitalPostSchemaValidator(new Validator(), getSchemaMap());
    }

    private Map<String, Schema> getSchemaMap() {
        SchemaStore schemaStore = new SchemaStore(getUrlRewriter());
        return Collections.unmodifiableMap(
                Arrays.stream(MessageType.values())
                        .collect(Collectors.toMap(MessageType::getType, p -> loadSchema(schemaStore, p.getSchemaUri()))));
    }

    private UrlRewriter getUrlRewriter() {
        return uri -> URI.create(String.format("classpath:/schema/%s%s", uri.getHost(), uri.getPath()));
    }

    @SneakyThrows
    private Schema loadSchema(SchemaStore schemaStore, URI schemaUri) {
        return schemaStore.loadSchema(schemaUri);
    }

    @Bean
    public TrustedCertificates trustedCertificates(ResourceLoader resourceLoader) {
        return new TrustedCertificates(resourceLoader);
    }

    @Bean
    public KeyPair keyPair(TrustedCertificates trustedCertificates) throws KeystoreProviderException {
        KeyStore keyStore = KeystoreProvider.loadKeyStore(properties.getKeystore());

        return new KeyPair()
                .setKeyStore(keyStore)
                .setTrustStore(getTrustStore()
                        .map(p -> addTrustedCertificates(p, trustedCertificates))
                        .orElseGet(trustedCertificates::getTrustStore))
                .setBusinessCertificateAlias(properties.getKeystore().getAlias())
                .setBusinessCertificatePassword(properties.getKeystore().getPassword());
    }

    private Optional<KeyStore> getTrustStore() {
        return Optional.ofNullable(properties.getTrustStore())
                .map(this::loadTrustStore);
    }

    private KeyStore addTrustedCertificates(KeyStore trustStore, TrustedCertificates trustedCertificates) {
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

    @Bean
    public FileExtensionMapper fileExtensionMapper() {
        return new FileExtensionMapper();
    }
}
