package no.digdir.dpi.client.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import no.digdir.dpi.client.domain.StandardBusinessDocumentWrapper;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

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
            return objectMapper.readValue(inputStream, StandardBusinessDocumentWrapper.class).getStandardBusinessDocument();
        }
    }

    @SneakyThrows
    public StandardBusinessDocument readStandardBusinessDocument(String s) {
        return objectMapper.readValue(s, StandardBusinessDocumentWrapper.class).getStandardBusinessDocument();
    }

    @SneakyThrows
    public Map<String, Object> convertToJsonObject(StandardBusinessDocument standardBusinessDocument) {
        return objectMapper.convertValue(new StandardBusinessDocumentWrapper(standardBusinessDocument), new TypeReference<Map<String, Object>>() {
        });
    }
}
