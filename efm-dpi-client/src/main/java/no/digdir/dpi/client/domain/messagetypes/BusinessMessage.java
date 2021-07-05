package no.digdir.dpi.client.domain.messagetypes;

import no.digdir.dpi.client.domain.sbd.Avsender;

public interface BusinessMessage<T extends BusinessMessage<T>> {

    Avsender getAvsender();

    BusinessMessage<T> setAvsender(Avsender avsender);
}
