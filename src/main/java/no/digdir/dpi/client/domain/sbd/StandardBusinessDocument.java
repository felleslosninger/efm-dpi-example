
package no.digdir.dpi.client.domain.sbd;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import no.digdir.dpi.client.domain.messagetypes.BusinessMessage;
import no.digdir.dpi.client.domain.sbd.header.StandardBusinessDocumentHeader;

import java.util.Optional;


@Data
@JsonSerialize(using = StandardBusinessDocumentSerializer.class)
public class StandardBusinessDocument {

    private StandardBusinessDocumentHeader standardBusinessDocumentHeader;

    @JsonDeserialize(using = MessageDeserializer.class)
    @JsonAlias({"digital", "utskrift"})
    private BusinessMessage<? extends BusinessMessage<?>> message;

    @JsonIgnore
    public String getType() {
        return Optional.of(standardBusinessDocumentHeader)
                .flatMap(p -> Optional.ofNullable(p.getDocumentIdentification()))
                .flatMap(p -> Optional.ofNullable(p.getType()))
                .orElse(null);
    }
}
