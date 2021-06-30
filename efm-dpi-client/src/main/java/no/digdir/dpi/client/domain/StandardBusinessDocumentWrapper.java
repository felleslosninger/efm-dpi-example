package no.digdir.dpi.client.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;

@Data
@NoArgsConstructor
public class StandardBusinessDocumentWrapper {

    private StandardBusinessDocument standardBusinessDocument;

    public StandardBusinessDocumentWrapper(StandardBusinessDocument standardBusinessDocument) {
        this.standardBusinessDocument = standardBusinessDocument;
    }
}
