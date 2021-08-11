package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.oauth.JwtTokenClient;
import org.springframework.cache.annotation.Cacheable;

@RequiredArgsConstructor
public class CreateMaskinportenToken {

    private final JwtTokenClient jwtTokenClient;

    @Cacheable("dpiClient.getMaskinportenToken")
    public String getMaskinportenToken() {
        return jwtTokenClient.fetchToken().getAccessToken();
    }
}
