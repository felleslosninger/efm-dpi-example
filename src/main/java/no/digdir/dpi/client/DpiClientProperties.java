package no.digdir.dpi.client;

import lombok.Data;
import no.difi.move.common.config.KeystoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.URL;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "dpi.client")
public class DpiClientProperties {

    @NotNull
    String uri;

    @Valid
    @NotNull
    private Timeout timeout;

    int temporaryFileThreshold = 10 * 1000 * 1000;
    File temporaryFileDirectory;
    int initialBufferSize = 100000;

    @Valid
    private KeystoreProperties keystore;

    @Valid
    private KeystoreProperties trustStore;

    @Valid
    private Oidc oidc;

    @Data
    public static class Oidc {

        @NotNull
        private boolean enable;
        private URL url;
        private String audience;
        private String clientId;
        private List<String> scopes;
        @NestedConfigurationProperty
        private KeystoreProperties keystore;
    }

    @Data
    public static class Timeout {

        private int connect;
        private int read;
        private int write;
    }
}
