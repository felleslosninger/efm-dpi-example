package no.digdir.dpi.client.internal;

import no.digdir.dpi.client.domain.sbd.Avsender;

public interface CreateMaskinportenToken {

    String createMaskinportenTokenForReceiving();

    String createMaskinportenTokenForSending(Avsender avsender);
}
