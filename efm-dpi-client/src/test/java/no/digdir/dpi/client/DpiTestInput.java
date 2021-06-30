package no.digdir.dpi.client;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.core.io.Resource;

import java.util.List;

@Value
@Builder
public class DpiTestInput {

    @NonNull Resource standardBusinessDocument;
    @NonNull Resource mainDocument;
    @NonNull List<Resource> attachments;
    @NonNull String mailbox;
    @NonNull Resource receiverCertificate;
}
