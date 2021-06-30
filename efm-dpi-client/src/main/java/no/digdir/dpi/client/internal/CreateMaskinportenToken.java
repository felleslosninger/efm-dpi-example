package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.move.common.oauth.JwtTokenClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateMaskinportenToken {

    private final JwtTokenClient jwtTokenClient;

    public String getMaskinportenToken() {
        return jwtTokenClient.fetchToken().getAccessToken();
    }
}
