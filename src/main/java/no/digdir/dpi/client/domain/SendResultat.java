package no.digdir.dpi.client.domain;

import lombok.Data;

@Data
public class SendResultat {

    String meldingsId;
    String referanseTilMeldingsId;
    long fakturerbareBytes;
}
