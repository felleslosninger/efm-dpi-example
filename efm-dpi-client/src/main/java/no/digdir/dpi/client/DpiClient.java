package no.digdir.dpi.client;

import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Message;
import no.digdir.dpi.client.domain.MessageStatus;
import no.digdir.dpi.client.domain.Shipment;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.UUID;

public interface DpiClient {
    void send(Shipment shipment) throws DpiException;

    Flux<MessageStatus> getMessageStatuses(UUID identifier) throws DpiException;

    Flux<Message> getMessages() throws DpiException;

    CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException;

    void markAsRead(UUID identifier) throws DpiException;
}
