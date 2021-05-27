package no.digdir.dpi.client.internal;

import lombok.SneakyThrows;
import no.digipost.api.representations.Dokumentpakke;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class CreateMultipart {

    private static final MediaType APPLICATION_JWT = MediaType.parseMediaType("application/jwt");
    private static final MediaType APPLICATION_CMS = MediaType.parseMediaType("application/cms");

    @SneakyThrows
    public MultiValueMap<String, HttpEntity<?>> createMultipart(String jwt, Dokumentpakke dokumentpakke) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("sbd", jwt, APPLICATION_JWT)
                .filename("sbd.jwt");
        builder.part("cms", new InputStreamResource(dokumentpakke.getInputStream()), APPLICATION_CMS)
                .filename(dokumentpakke.getName());
        return builder.build();
    }
}
