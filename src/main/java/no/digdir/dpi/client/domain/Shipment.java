package no.digdir.dpi.client.domain;

import lombok.Data;
import no.digdir.dpi.client.domain.sbd.StandardBusinessDocument;

@Data
public class Shipment {

    StandardBusinessDocument standardBusinessDocument;
    Parcel parcel;
    String mailbox;
    BusinessCertificate receiverBusinessCertificate;
    String language = "NO";
}
