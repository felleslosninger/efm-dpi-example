package no.digdir.dpi.example;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class DpiExampleInput {

    @NonNull Files files;
    @NonNull String postkasseadresse;
    @NonNull String sertifikat;

    @Value
    public static class Files {
        @NonNull List<File> fileList;

        public Files(@NonNull List<String> paths) {
            Assert.isTrue(paths.size() >= 2, "Must atl");
            this.fileList = paths.stream().map(File::new).collect(Collectors.toList());
        }

        File getStandardBusinessDocument() {
            return fileList.get(0);
        }

        File getHoveddokument() {
            return fileList.get(1);
        }

        List<File> getVedlegg() {
            return fileList.size() >= 2 ? fileList.subList(2, fileList.size()) : Collections.emptyList();
        }
    }
}
