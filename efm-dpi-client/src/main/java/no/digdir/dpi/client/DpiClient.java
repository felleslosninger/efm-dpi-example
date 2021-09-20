package no.digdir.dpi.client;

import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.MessageStatus;
import no.digdir.dpi.client.domain.ReceivedMessage;
import no.digdir.dpi.client.domain.Shipment;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.UUID;

public interface DpiClient {
    void sendMessage(Shipment shipment) throws DpiException;

    Flux<MessageStatus> getMessageStatuses(UUID messageId) throws DpiException;

    Flux<ReceivedMessage> getMessages(String avsenderidentifikator) throws DpiException;

    CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException;

    void markAsRead(UUID messageId) throws DpiException;
}
