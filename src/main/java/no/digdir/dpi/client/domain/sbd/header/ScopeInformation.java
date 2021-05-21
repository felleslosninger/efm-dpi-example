
package no.digdir.dpi.client.domain.sbd.header;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class ScopeInformation {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    OffsetDateTime expectedResponseDateTime;
}
