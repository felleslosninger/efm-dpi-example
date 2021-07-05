package no.digdir.dpi.client.domain.messagetypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import no.digdir.dpi.client.domain.sbd.Avsender;
import no.digdir.dpi.client.domain.sbd.Virksomhetmottaker;

import java.time.OffsetDateTime;

abstract class AbstractKvittering<T extends Kvittering<T>> implements Kvittering<T> {

    private Avsender avsender;
    private Virksomhetmottaker mottaker;
    private String maskinportentoken;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime tidspunkt;

    @Override
    public Avsender getAvsender() {
        return avsender;
    }

    public BusinessMessage<T> setAvsender(Avsender avsender) {
        this.avsender = avsender;
        return this;
    }

    @Override
    public Virksomhetmottaker getMottaker() {
        return mottaker;
    }

    public BusinessMessage<T> setMottaker(Virksomhetmottaker mottaker) {
        this.mottaker = mottaker;
        return this;
    }

    @Override
    public String getMaskinportentoken() {
        return maskinportentoken;
    }

    @Override
    public BusinessMessage<T> setMaskinportentoken(String maskinportentoken) {
        this.maskinportentoken = maskinportentoken;
        return this;
    }

    @Override
    public OffsetDateTime getTidspunkt() {
        return tidspunkt;
    }

    public BusinessMessage<T> setTidspunkt(OffsetDateTime tidspunkt) {
        this.tidspunkt = tidspunkt;
        return this;
    }
}
