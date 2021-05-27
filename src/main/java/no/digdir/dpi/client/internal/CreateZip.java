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
                log.trace("Adding " + file.getFilename() + " to archive. Size in bytes before compression: " + file.getBytes().length);
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(file.getFilename());
                zipEntry.setSize(file.getBytes().length);

                zipOutputStream.putArchiveEntry(zipEntry);
                IOUtils.write(file.getBytes(), zipOutputStream);
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
