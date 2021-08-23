package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateMaskinportenTokenMock implements CreateMaskinportenToken {

    private final String token;

    @Override
    public String createMaskinportenToken() {
        return token;
    }
}
