
package no.digdir.dpi.client.domain.sbd.header;

import lombok.Data;

import java.util.List;

@Data
public class StandardBusinessDocumentHeader {

    String headerVersion = "1.0";
    List<Actor> sender;
    List<Actor> receiver;
    DocumentIdentification documentIdentification;
    BusinessScope businessScope;
}
