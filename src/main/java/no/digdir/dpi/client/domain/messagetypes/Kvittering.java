package no.digdir.dpi.client.domain.messagetypes;


import no.digdir.dpi.client.domain.sbd.Avsender;
import no.digdir.dpi.client.domain.sbd.Virksomhetmottaker;

import java.time.OffsetDateTime;

interface Kvittering<T extends BusinessMessage<T>> extends BusinessMessage<T>, MaskinportentokenHolder {

    Avsender getAvsender();

    BusinessMessage<T> setAvsender(Avsender avsender);

    Virksomhetmottaker getMottaker();

    BusinessMessage<T> setMottaker(Virksomhetmottaker mottaker);

    String getMaskinportentoken();

    BusinessMessage<T> setMaskinportentoken(String maskinportentoken);

    OffsetDateTime getTidspunkt();

    BusinessMessage<T> setTidspunkt(OffsetDateTime tidspunkt);
}
