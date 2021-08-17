package no.digdir.dpi.client.internal;

public class CreateMaskinportenTokenMock implements CreateMaskinportenToken {

    @Override
    public String createMaskinportenToken() {
        return "DummyToken";
    }
}
