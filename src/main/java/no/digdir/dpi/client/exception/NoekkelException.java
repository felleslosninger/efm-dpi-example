package no.digdir.dpi.client.exception;

public class NoekkelException extends KonfigurasjonException {

    public NoekkelException(String message, Exception e) {
        super(message, e);
    }

    public NoekkelException(String s) {
        super(s);
    }

}
