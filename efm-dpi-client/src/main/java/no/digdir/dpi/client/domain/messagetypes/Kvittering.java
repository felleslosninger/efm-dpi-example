package no.digdir.dpi.client.domain.messagetypes;


import no.digdir.dpi.client.domain.sbd.Virksomhetmottaker;

import java.time.OffsetDateTime;

interface Kvittering extends BusinessMessage, MaskinportentokenHolder {

    Virksomhetmottaker getMottaker();

    OffsetDateTime getTidspunkt();
}
