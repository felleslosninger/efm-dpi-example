package no.digdir.dpi.client.internal;

import no.digdir.dpi.client.DpiException;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Message;
import no.digdir.dpi.client.domain.MessageStatus;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.UUID;

public interface Corner2Client {
    void sendMessage(String jwt, CmsEncryptedAsice cmsEncryptedAsice);

    Flux<MessageStatus> getMessageStatuses(UUID identifier);

    Flux<Message> getMessages();

    CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException;

    void markAsRead(UUID identifier);
}
