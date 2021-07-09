package no.digdir.dpi.client;

import no.digdir.dpi.client.domain.*;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.UUID;

public interface DpiClient {
    void sendMessage(Shipment shipment) throws DpiException;

    Flux<MessageStatus> getMessageStatuses(UUID identifier) throws DpiException;

    Flux<ReceivedMessage> getMessages() throws DpiException;

    CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException;

    void markAsRead(UUID identifier) throws DpiException;
}
