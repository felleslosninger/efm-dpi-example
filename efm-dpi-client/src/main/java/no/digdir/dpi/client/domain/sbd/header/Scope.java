
package no.digdir.dpi.client.domain.sbd.header;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Scope {

    private String type;
    private UUID instanceIdentifier;
    private  String identifier;
    private List<ScopeInformation> scopeInformation;
}
