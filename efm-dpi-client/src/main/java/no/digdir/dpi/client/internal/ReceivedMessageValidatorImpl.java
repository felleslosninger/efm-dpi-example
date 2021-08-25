package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.sbdh.PartnerIdentification;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocumentUtils;
import no.digdir.dpi.client.domain.ReceivedMessage;
import no.digdir.dpi.client.domain.messagetypes.BusinessMessage;
import no.digdir.dpi.client.domain.messagetypes.MaskinportentokenHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Optional;

@RequiredArgsConstructor
public class ReceivedMessageValidatorImpl implements ReceivedMessageValidator {

    private final JwtDecoder jwtDecoder;
    private final JwtClaimService jwtClaimService;

    @Override
    public void validate(ReceivedMessage receivedMessage) {
        StandardBusinessDocument sbd = receivedMessage.getStandardBusinessDocument();
        Jwt jwt = sbd.getBusinessMessage(MaskinportentokenHolder.class)
                .map(MaskinportentokenHolder::getMaskinportentoken)
                .map(jwtDecoder::decode)
                .orElseThrow(() -> new ReceivedMessageValidator.Exception(receivedMessage, "Missing Maskinportentoken!"));
        assertCorrectAvsender(receivedMessage, jwt);
        assertCorrectDatabehandler(receivedMessage, jwt);
    }

    private void assertCorrectDatabehandler(ReceivedMessage receivedMessage, Jwt jwt) {
        StandardBusinessDocument sbd = receivedMessage.getStandardBusinessDocument();
        PartnerIdentification expectedDatabehandler = jwtClaimService.getDatabehandler(jwt.getClaims());
        PartnerIdentification actualDatabehandler = StandardBusinessDocumentUtils.getFirstSender(sbd)
                .flatMap(p -> Optional.ofNullable(p.getIdentifier()))
                .orElseThrow(() -> new ReceivedMessageValidator.Exception(receivedMessage, "Missing sender in SBDH!"));

        if (!actualDatabehandler.equals(expectedDatabehandler)) {
            throw new ReceivedMessageValidator.Exception(receivedMessage,
                    String.format("Invalid databehandler. expected=%s actual=%s", actualDatabehandler, expectedDatabehandler)
            );
        }
    }

    private void assertCorrectAvsender(ReceivedMessage receivedMessage, Jwt jwt) {
        StandardBusinessDocument sbd = receivedMessage.getStandardBusinessDocument();
        PartnerIdentification expectedAvsender = jwtClaimService.getAvsender(jwt.getClaims());
        PartnerIdentification actualAvsender = sbd.getBusinessMessage(BusinessMessage.class)
                .flatMap(p -> Optional.ofNullable(p.getAvsender()))
                .flatMap(p -> Optional.ofNullable(p.getVirksomhetsidentifikator()))
                .map(p -> new PartnerIdentification()
                        .setAuthority(p.getAuthority())
                        .setValue(p.getValue())
                )
                .orElseThrow(() -> new ReceivedMessageValidator.Exception(receivedMessage, "Missing avsender in BusinessMessage!"));

        if (!actualAvsender.equals(expectedAvsender)) {
            throw new ReceivedMessageValidator.Exception(receivedMessage,
                    String.format("Invalid avsender. expected=%s actual=%s", expectedAvsender, actualAvsender));
        }
    }
}
