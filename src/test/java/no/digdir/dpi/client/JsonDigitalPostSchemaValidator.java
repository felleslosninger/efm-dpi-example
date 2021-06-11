package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.ValidationException;
import net.jimblackler.jsonschemafriend.Validator;

@RequiredArgsConstructor
public class JsonDigitalPostSchemaValidator {

    private final Validator validator;
    private final Schema schema;

    public void validate(Object document) throws ValidationException {
        validator.validate(schema, document);
    }
}
