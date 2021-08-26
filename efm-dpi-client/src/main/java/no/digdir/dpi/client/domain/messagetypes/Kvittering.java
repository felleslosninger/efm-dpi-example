package no.digdir.dpi.client.domain.messagetypes;


import no.digdir.dpi.client.domain.sbd.Virksomhetmottaker;

import java.time.OffsetDateTime;

public interface Kvittering extends BusinessMessage {

    Virksomhetmottaker getMottaker();

    OffsetDateTime getTidspunkt();
}
