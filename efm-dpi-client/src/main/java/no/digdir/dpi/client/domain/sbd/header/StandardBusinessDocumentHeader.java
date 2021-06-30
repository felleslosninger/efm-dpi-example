
package no.digdir.dpi.client.domain.sbd.header;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class StandardBusinessDocumentHeader {

    private String headerVersion = "1.0";
    private List<Actor> sender;
    private List<Actor> receiver;
    private DocumentIdentification documentIdentification;
    private BusinessScope businessScope;

    @JsonIgnore
    public String getType() {
        return Optional.ofNullable(documentIdentification)
                .flatMap(p -> Optional.ofNullable(p.getType()))
                .orElse(null);
    }
}
