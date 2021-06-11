
package no.digdir.dpi.client.domain.sbd.header;

import lombok.Data;

import java.util.List;

@Data
public class StandardBusinessDocumentHeader {

    private String headerVersion = "1.0";
    private List<Actor> sender;
    private List<Actor> receiver;
    private DocumentIdentification documentIdentification;
    private BusinessScope businessScope;
}
