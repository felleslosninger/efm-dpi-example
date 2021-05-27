package no.digdir.dpi.client;

import lombok.Data;
import no.difi.move.common.config.KeystoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Component
@ConfigurationProperties(prefix = "dpi.client")
public class DpiClientProperties {

    @NotNull
    String uri;

    @Valid
    private KeystoreProperties keystore;

    @Valid
    private KeystoreProperties trustStore;
}
