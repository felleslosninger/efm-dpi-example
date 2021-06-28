
package no.digdir.dpi.client.domain.sbd.header;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import no.digdir.dpi.client.domain.sbd.TypeDeserializer;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
public class DocumentIdentification {

    private String standard;
    private String typeVersion;
    private UUID instanceIdentifier;

    @JsonDeserialize(using = TypeDeserializer.class)
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime creationDateAndTime;
}
