package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.oauth.JwtTokenClient;
import org.springframework.cache.annotation.Cacheable;

@RequiredArgsConstructor
public class CreateMaskinportenTokenImpl implements CreateMaskinportenToken {

    private final JwtTokenClient jwtTokenClient;

    @Override
    @Cacheable("dpiClient.getMaskinportenToken")
    public String createMaskinportenToken() {
        return jwtTokenClient.fetchToken().getAccessToken();
    }
}
