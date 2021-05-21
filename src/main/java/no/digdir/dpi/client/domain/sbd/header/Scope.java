
package no.digdir.dpi.client.domain.sbd.header;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Scope {

    String type;
    UUID instanceIdentifier;
    String identifier;
    List<ScopeInformation> scopeInformation;
}
