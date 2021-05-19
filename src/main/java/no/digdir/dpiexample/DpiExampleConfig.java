package no.digdir.dpiexample;

import no.difi.sdp.client2.internal.CreateDokumentpakke;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class DpiExampleConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Europe/Oslo"));
    }

    @Bean
    public CreateDokumentpakke createDokumentpakke() {
        return new CreateDokumentpakke();
    }
}
