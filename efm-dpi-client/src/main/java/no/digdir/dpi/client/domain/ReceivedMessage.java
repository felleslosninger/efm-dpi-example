package no.digdir.dpi.client.domain;

import lombok.Data;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;

@Data
public class ReceivedMessage {

    private Message message;
    private StandardBusinessDocument standardBusinessDocument;
}
