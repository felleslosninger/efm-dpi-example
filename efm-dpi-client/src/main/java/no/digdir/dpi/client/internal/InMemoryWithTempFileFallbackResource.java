package no.digdir.dpi.client.internal;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.WritableResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@RequiredArgsConstructor
public class InMemoryWithTempFileFallbackResource extends AbstractResource implements WritableResource {

    private final DeferredFileOutputStream deferredFileOutputStream;

    @NonNull
    @Override
    public OutputStream getOutputStream() {
        return deferredFileOutputStream;
    }

    @NonNull
    @Override
    public File getFile() throws IOException {
        return deferredFileOutputStream.isInMemory() ? super.getFile() : deferredFileOutputStream.getFile();
    }

    @NonNull
    @Override
    public String getDescription() {
        if (deferredFileOutputStream.isInMemory()) {
            return "byte [" + deferredFileOutputStream.getByteCount() + "]";
        }

        return "file [" + deferredFileOutputStream.getFile().getAbsolutePath() + "]";
    }

    @NonNull
    @Override
    public InputStream getInputStream() throws IOException {
        return deferredFileOutputStream.isInMemory()
                ? new ByteArrayInputStream(deferredFileOutputStream.getData())
                : Files.newInputStream(deferredFileOutputStream.getFile().toPath(), StandardOpenOption.READ);
    }

    @Override
    public long contentLength() {
        return deferredFileOutputStream.getByteCount();
    }

    public void deleteFileIfItExists() throws IOException {
        if (!deferredFileOutputStream.isInMemory()) {
            Files.delete(deferredFileOutputStream.getFile().toPath());
        }
    }
}