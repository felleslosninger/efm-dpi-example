package no.digdir.dpi.client.internal;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateInstanceIdentifier {

    public String createInstanceIdentifier() {
        return UUID.randomUUID().toString();
    }
}
