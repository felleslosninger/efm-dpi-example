package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.DpiClientProperties;
import no.digdir.dpi.client.DpiException;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Message;
import no.digdir.dpi.client.domain.MessageStatus;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RequiredArgsConstructor
public class LocalDirectoryCorner2Client implements Corner2Client {

    private final DpiClientProperties properties;

    @Override
    public void sendMessage(String jwt, CmsEncryptedAsice cmsEncryptedAsice) {
        String base = UUID.randomUUID().toString();
        writeJWT(jwt, base);
        writeCmsEncryptedAsice(base, cmsEncryptedAsice);
    }

    private void writeJWT(String jwt, String base) {
        try {
            Files.write(getPath(base, "jwt"), jwt.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new Exception("Couldn't save file!", e);
        }
    }

    private void writeCmsEncryptedAsice(String base, CmsEncryptedAsice cmsEncryptedAsice) {
        try (InputStream is = cmsEncryptedAsice.getResource().getInputStream()) {
            Files.copy(is, getPath(base, "asic.cms"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new Exception("Couldn't save file!", e);
        }
    }

    private Path getPath(String base, String postfix) {
        try {
            File targetFile = new File(new File(URI.create(properties.getUri()).toURL().getFile()), base + "." + postfix);
            return targetFile.toPath();
        } catch (MalformedURLException e) {
            throw new Exception("Malformed URL", e);
        }
    }

    @Override
    public Flux<MessageStatus> getMessageStatuses(UUID identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flux<Message> getMessages() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CmsEncryptedAsice getCmsEncryptedAsice(URI downloadurl) throws DpiException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void markAsRead(UUID identifier) {
        throw new UnsupportedOperationException();
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
