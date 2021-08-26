package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.sbd.Avsender;

@RequiredArgsConstructor
public class CreateMaskinportenTokenMock implements CreateMaskinportenToken {

    private final String token;

    @Override
    public String createMaskinportenTokenForReceiving() {
        return token;
    }

    @Override
    public String createMaskinportenTokenForSending(Avsender avsender) {
        return token;
    }
}
