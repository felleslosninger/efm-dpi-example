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
        // return jwtTokenClient.fetchToken().getAccessToken();
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    }
}
