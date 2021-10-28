package no.digdir.dpi.example;

import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.dpi.client.DpiClientConfig;
import no.difi.meldingsutveksling.dpi.client.DpiClientProperties;
import no.difi.meldingsutveksling.dpi.client.FileExtensionMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@RequiredArgsConstructor
@Import({
        DpiClientConfig.class,
        DpiClientProperties.class,
        ShipmentFactory.class,
        FileExtensionMapper.class
})
public class DpiExampleConfig {

    @Bean
    @ConditionalOnMissingBean
    public Clock clock() {
        return Clock.system(ZoneId.of("Europe/Oslo"));
    }
}