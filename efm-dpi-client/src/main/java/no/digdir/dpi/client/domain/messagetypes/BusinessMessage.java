package no.digdir.dpi.client.domain.messagetypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.digdir.dpi.client.domain.sbd.Avsender;

public interface BusinessMessage<T extends BusinessMessage<T>> {

    Avsender getAvsender();

    @JsonIgnore
    MessageType getMessageType();
}