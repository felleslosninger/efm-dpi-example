package no.digdir.dpi.client.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DpiMapper {

    @Getter(AccessLevel.PACKAGE)
    private final ObjectMapper objectMapper;

    public DpiMapper() {
        this.objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }

    @SneakyThrows
    public StandardBusinessDocument readStandardBusinessDocument(File file) {
        return objectMapper.readValue(file, StandardBusinessDocument.class);
    }

    @SneakyThrows
    public String writeStandardBusinessDocument(StandardBusinessDocument standardBusinessDocument) {
        return objectMapper.writeValueAsString(standardBusinessDocument);
    }
}
