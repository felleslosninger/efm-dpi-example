package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.ValidationException;
import net.jimblackler.jsonschemafriend.Validator;

@RequiredArgsConstructor
public class JsonDigitalPostSchemaValidator {

    private final Validator validator;
    private final Schema schema;

    public void validate(Object document) {
        try {
            validator.validate(schema, document);
        } catch (ValidationException e) {
            throw new JsonDigitalPostSchemaValidator.Exception("Validation of Digital Post SBD failed!", e);
        }
    }

    private static class Exception extends RuntimeException {

        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
