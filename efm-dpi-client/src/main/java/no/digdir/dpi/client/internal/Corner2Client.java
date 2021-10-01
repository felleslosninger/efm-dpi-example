package no.digdir.dpi.client.internal;

import no.digdir.dpi.client.DpiException;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.GetMessagesInput;
import no.digdir.dpi.client.domain.Message;
import no.digdir.dpi.client.domain.MessageStatus;
import no.digdir.dpi.client.internal.domain.SendMessageInput;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.UUID;

public interface Corner2Client {
    void sendMessage(SendMessageInput input);

    Flux<MessageStatus> getMessageStatuses(UUID messageId);

    Flux<Message> getMessages(GetMessagesInput input);

    CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException;

    void markAsRead(UUID messageId);
}
