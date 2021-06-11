
package no.digdir.dpi.client.domain.sbd.header;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
public class DocumentIdentification {

    private String standard;
    private String typeVersion;
    private UUID instanceIdentifier;
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime creationDateAndTime;
}
