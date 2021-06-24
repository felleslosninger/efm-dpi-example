package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.oauth.JwtTokenClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateMaskinportenToken {

    private final JwtTokenClient jwtTokenClient;

    public String getMaskinportenToken() {
        // todo: bytt med en tilfeldig streng
        return jwtTokenClient.fetchToken().getAccessToken();
    }
}
