
package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import no.digdir.dpi.client.domain.sbd.header.StandardBusinessDocumentHeader;


@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(value = "standardBusinessDocument")
public class StandardBusinessDocument {

    private StandardBusinessDocumentHeader standardBusinessDocumentHeader;
    private Digitalpost digitalpost;
}
