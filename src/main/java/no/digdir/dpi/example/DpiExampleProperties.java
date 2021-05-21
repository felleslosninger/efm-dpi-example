package no.digdir.dpi.example;

import lombok.Data;
import no.difi.move.common.config.KeystoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Component
@ConfigurationProperties(prefix = "example")
public class DpiExampleProperties {

    @Valid
    private Organization org;

    @Valid
    private DigitalPostInnbyggerConfig dpi;

    @Data
    public static class Organization {

        /**
         * Organization number to run as.
         */
        @NotNull
        private String identifier;

        /**
         * Business certificate for this instance.
         */
        @Valid
        @NotNull(message = "Certificate properties not set.")
        private KeystoreProperties keystore;
    }

    @Data
    public static class DigitalPostInnbyggerConfig {

        @Valid
        private KeystoreProperties keystore;

        @Valid
        private KeystoreProperties trustStore;
    }
}
