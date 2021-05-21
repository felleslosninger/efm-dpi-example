package no.digdir.dpi.client.exception;

import java.io.IOException;

/**
 * Wrapper for IOExceptions i situasjoner der det er ingen/liten praktisk grunn til å anta at en IOException kan oppstå (f.eks. minnerepresentasjoner av streams).
 */
public class RuntimeIOException extends SikkerDigitalPostException {

    public RuntimeIOException(IOException e) {
        super(e);
    }

}
