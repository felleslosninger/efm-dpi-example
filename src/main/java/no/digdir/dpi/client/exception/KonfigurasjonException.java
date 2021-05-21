package no.digdir.dpi.client.exception;

public class KonfigurasjonException extends SikkerDigitalPostException {

    public KonfigurasjonException(String message, Exception e) {
        super(message, e);
    }

    public KonfigurasjonException(String message) {
        this(message, null);
    }

    public KonfigurasjonException(Exception e) {
        this(null, e);
    }

}
