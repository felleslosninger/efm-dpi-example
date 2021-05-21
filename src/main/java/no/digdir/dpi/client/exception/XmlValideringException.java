package no.digdir.dpi.client.exception;

public class XmlValideringException extends SendException {
    public XmlValideringException(String message, AntattSkyldig antattSkyldig, Exception e) {
        super(message, antattSkyldig, e);
    }
}
