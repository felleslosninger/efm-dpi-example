package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Message<T extends Message<T>> {

    default T setDokumentpakkefingeravtrykk(Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk) {
        throw new UnsupportedOperationException();
    }

    default T setMaskinportentoken(String maskinportenToken) {
        throw new UnsupportedOperationException();
    }

    default Avsender getAvsender() {
        throw new UnsupportedOperationException();
    }

    @JsonIgnore
    MessageType getMessageType();
}
