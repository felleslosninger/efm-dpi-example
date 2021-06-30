
package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import no.digdir.dpi.client.domain.messagetypes.BusinessMessage;
import no.digdir.dpi.client.domain.sbd.header.StandardBusinessDocumentHeader;

import java.util.Optional;


@Data
public class StandardBusinessDocument {

    private StandardBusinessDocumentHeader standardBusinessDocumentHeader;
    private BusinessMessage<? extends BusinessMessage<?>> businessMessage;

    @JsonIgnore
    public String getType() {
        return Optional.of(standardBusinessDocumentHeader)
                .flatMap(p -> Optional.ofNullable(p.getType()))
                .orElse(null);
    }
}
