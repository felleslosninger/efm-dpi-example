package no.digdir.dpi.client.exception;

public class SertifikatException extends KonfigurasjonException {

    public SertifikatException(String message, Exception e) {
        super(message, e);
    }

    public SertifikatException(String message) {
        super(message);
    }

}
