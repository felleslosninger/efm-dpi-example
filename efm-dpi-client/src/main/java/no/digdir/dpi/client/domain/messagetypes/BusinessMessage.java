package no.digdir.dpi.client.domain.messagetypes;

import no.digdir.dpi.client.domain.sbd.Avsender;

public interface BusinessMessage {

    /**
     * Sender. When sending on behalf of an organization, this field will contain the "on behalf of"-organization.
     */
    Avsender getAvsender();
}
