package no.digdir.dpi.client;

import lombok.Data;
import no.difi.move.common.config.KeystoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Component
@ConfigurationProperties(prefix = "dpi.server")
public class DpiServerProperties {

    @Valid
    private KeystoreProperties keystore;
}
