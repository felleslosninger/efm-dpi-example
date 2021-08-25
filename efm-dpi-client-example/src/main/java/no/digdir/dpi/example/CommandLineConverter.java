package no.digdir.dpi.example;

import lombok.NonNull;
import lombok.Value;
import org.apache.commons.cli.CommandLine;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommandLineConverter {

    public DpiExampleInput toDpiExampleInput(CommandLine commandLine) {
        Resources resources = new Resources(commandLine.getArgList());

        return DpiExampleInput.builder()
                .standardBusinessDocument(resources.getStandardBusinessDocument())
                .mainDocument(resources.getMainDocument())
                .attachments(resources.getAttachments())
                .receiverCertificate(new FileSystemResource(commandLine.getOptionValue("certificate")))
                .build();
    }

    @Value
    public static class Resources {
        @NonNull List<Resource> resourceList;

        public Resources(@NonNull List<String> paths) {
            Assert.isTrue(paths.size() >= 2, "Must atl");
            this.resourceList = paths.stream().map(FileSystemResource::new).collect(Collectors.toList());
        }

        Resource getStandardBusinessDocument() {
            return resourceList.get(0);
        }

        Resource getMainDocument() {
            return resourceList.get(1);
        }

        List<Resource> getAttachments() {
            return resourceList.size() >= 2 ? resourceList.subList(2, resourceList.size()) : Collections.emptyList();
        }
    }
}
