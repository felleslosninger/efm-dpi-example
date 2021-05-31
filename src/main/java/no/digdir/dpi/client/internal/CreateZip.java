package no.digdir.dpi.client.internal;

import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.AsicEAttachable;
import no.digdir.dpi.client.internal.domain.Archive;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class CreateZip {

    public Archive zipIt(List<AsicEAttachable> files) {
        try (ByteArrayOutputStream archive = new ByteArrayOutputStream(); ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(archive)) {
            zipOutputStream.setEncoding(StandardCharsets.UTF_8.name());
            zipOutputStream.setMethod(ZipArchiveOutputStream.DEFLATED);
            for (AsicEAttachable file : files) {
                log.trace("Adding " + file.getFilename() + " to archive.");
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(file.getFilename());
                zipOutputStream.putArchiveEntry(zipEntry);
                try(InputStream inputStream = file.getInputStream()) {
                    IOUtils.copy(inputStream, zipOutputStream);
                }
                zipOutputStream.closeArchiveEntry();
            }
            zipOutputStream.finish();
            return new Archive(archive.toByteArray());
        } catch (IOException e) {
            throw new Exception("Failed to create XIP!", e);
        }
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
