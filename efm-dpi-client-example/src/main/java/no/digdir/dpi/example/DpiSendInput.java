package no.digdir.dpi.example;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.core.io.Resource;

import java.util.List;

@Value
@Builder
public class DpiSendInput {

    @NonNull Resource standardBusinessDocument;
    @NonNull Resource mainDocument;
    @NonNull List<Resource> attachments;
    @NonNull Resource receiverCertificate;
}
