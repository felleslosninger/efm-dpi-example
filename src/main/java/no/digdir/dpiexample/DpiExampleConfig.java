package no.digdir.dpiexample;

import no.difi.sdp.client2.internal.CreateDokumentpakke;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DpiExampleConfig {

    @Bean
    public CreateDokumentpakke createDokumentpakke() {
        return new CreateDokumentpakke();
    }
}
