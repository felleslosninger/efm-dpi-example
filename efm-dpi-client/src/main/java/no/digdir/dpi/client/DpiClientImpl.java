package no.digdir.dpi.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.MessageStatus;
import no.digdir.dpi.client.domain.ReceivedMessage;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.*;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DpiClientImpl implements DpiClient {

    private final CreateCmsEncryptedAsice createCmsEncryptedAsice;
    private final CreateMaskinportenToken createMaskinportenToken;
    private final CreateStandardBusinessDocument createStandardBusinessDocument;
    private final CreateStandardBusinessDocumentJWT createStandardBusinessDocumentJWT;
    private final Corner2Client corner2Client;
    private final MessageUnwrapper messageUnwrapper;

    @Override
    @SneakyThrows
    public void sendMessage(Shipment shipment) {
        try (CmsEncryptedAsice cmsEncryptedAsice = createCmsEncryptedAsice.createCmsEncryptedAsice(shipment)) {
//            String maskinportenToken = createMaskinportenToken.createMaskinportenTokenForSending(shipment.getBusinessMessage().getAvsender());
            String maskinportenToken = createMaskinportenToken.createMaskinportenTokenForReceiving();
            StandardBusinessDocument sbd = createStandardBusinessDocument.createStandardBusinessDocument(shipment);
            String jwt = createStandardBusinessDocumentJWT.createStandardBusinessDocumentJWT(sbd, cmsEncryptedAsice, maskinportenToken);
            corner2Client.sendMessage(maskinportenToken, jwt, cmsEncryptedAsice);
        } catch (DpiException e) {
            throw e;
        } catch (Exception e) {
            throw new DpiException("Sending failed!", e, Blame.CLIENT);
        }
    }

    @Override
    public Flux<MessageStatus> getMessageStatuses(UUID identifier) {
        return corner2Client.getMessageStatuses(identifier);
    }

    @Override
    public Flux<ReceivedMessage> getMessages() {
        return corner2Client.getMessages()
                .map(messageUnwrapper::unwrap);
    }

    @Override
    public CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException {
        return corner2Client.getCmsEncryptedAsice(downloadurl);
    }

    @Override
    public void markAsRead(UUID identifier) {
        corner2Client.markAsRead(identifier);
    }
}
