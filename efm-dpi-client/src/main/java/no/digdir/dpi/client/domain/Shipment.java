package no.digdir.dpi.client.domain;

import lombok.Data;
import no.difi.meldingsutveksling.domain.sbdh.StandardBusinessDocument;

@Data
public class Shipment {

    StandardBusinessDocument standardBusinessDocument;
    Parcel parcel;
    BusinessCertificate receiverBusinessCertificate;
    String language = "NO";
}
