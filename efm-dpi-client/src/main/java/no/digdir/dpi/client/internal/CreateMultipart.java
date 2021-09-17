package no.digdir.dpi.client.internal;

import lombok.SneakyThrows;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
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
    public MultiValueMap<String, HttpEntity<?>> createMultipart(String jwt, CmsEncryptedAsice cmsEncryptedAsice) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("forretningsmelding", jwt, APPLICATION_JWT)
                .filename("sbd.jwt");
        builder.part("dokumentpakke", cmsEncryptedAsice.getResource(), APPLICATION_CMS)
                .filename("asic.cms");
        return builder.build();
    }
}
