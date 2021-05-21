
package no.digdir.dpi.client.domain.sbd.header;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
public class DocumentIdentification {

    String standard;
    String typeVersion;
    UUID instanceIdentifier;
    String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    OffsetDateTime creationDateAndTime;
}
