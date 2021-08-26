package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.Iso6523;
import no.digdir.dpi.client.domain.sbd.Avsender;

import java.util.Optional;

@RequiredArgsConstructor
public class CreateOidcClientId {

    private final String clientIdPrefix;

    public String createOidcClientId(Avsender avsender) {
        Iso6523 iso6523 = Optional.ofNullable(avsender)
                .flatMap(p -> Optional.ofNullable(p.getVirksomhetsidentifikator()))
                .flatMap(p -> Optional.ofNullable(p.getValue()))
                .map(Iso6523::parse)
                .orElseThrow(() -> new Exception("Missing businessMessage.avsender.virksomhetsidentifikator.value!"));

        return clientIdPrefix + iso6523.getOrganizationIdentifier();
    }

    private static class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }
    }
}
