package no.digdir.dpi.client.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.InputStream;

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
    public StandardBusinessDocument readStandardBusinessDocument(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, StandardBusinessDocument.class);
        }
    }

    @SneakyThrows
    public StandardBusinessDocument readStandardBusinessDocument(String s) {
        return objectMapper.readValue(s, StandardBusinessDocument.class);
    }

    @SneakyThrows
    public String writeStandardBusinessDocument(StandardBusinessDocument standardBusinessDocument) {
        return objectMapper.writeValueAsString(standardBusinessDocument);
    }
}
