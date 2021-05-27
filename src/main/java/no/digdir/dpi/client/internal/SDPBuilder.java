package no.digdir.dpi.client.internal;

import no.difi.begrep.sdp.schema_v10.*;
import no.digdir.dpi.client.domain.Document;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.sbd.Avsender;
import no.digdir.dpi.client.domain.sbd.Identifikator;
import no.digdir.dpi.client.domain.sbd.Mottaker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SDPBuilder {

    public SDPManifest createManifest(Shipment shipment) {
        return new SDPManifest(
                getSdpMottaker(shipment),
                sdpAvsender(getAvsender(shipment)),
                getHoveddokument(shipment),
                getSdpVedlegg(shipment), null);
    }

    private Avsender getAvsender(Shipment shipment) {
        return shipment.getStandardBusinessDocument().getDigitalpost().getAvsender();
    }

    private SDPDokument getHoveddokument(Shipment shipment) {
        return sdpDokument(shipment.getParcel().getMainDocument(), shipment.getLanguage());
    }

    private List<SDPDokument> getSdpVedlegg(Shipment shipment) {
        return shipment.getParcel().getAttachments()
                .stream()
                .map(document -> sdpDokument(document, shipment.getLanguage()))
                .collect(Collectors.toList());
    }

    private SDPDokument sdpDokument(final Document document, final String spraakkode) {
        return new SDPDokument(
                getSdpTittel(document, spraakkode),
                getSdpDokumentData(document),
                document.getFilename(),
                document.getMimeType());
    }

    private SDPDokumentData getSdpDokumentData(Document document) {
        return Optional.ofNullable(document.getMetadataDocument())
                .map(d -> new SDPDokumentData(d.getFilename(), d.getMimeType()))
                .orElse(null);
    }

    private SDPTittel getSdpTittel(Document document, String spraakkode) {
        return document.getTitle() != null ? new SDPTittel(document.getTitle(), spraakkode) : null;
    }

    private SDPMottaker getSdpMottaker(Shipment shipment) {
        return new SDPMottaker(getSdpPerson(shipment));
    }

    private SDPPerson getSdpPerson(Shipment shipment) {
        Mottaker mottaker = shipment.getStandardBusinessDocument().getDigitalpost().getMottaker();
        return new SDPPerson(mottaker.getPersonidentifikator().getValue(), shipment.getMailbox());
    }

    private SDPAvsender sdpAvsender(Avsender avsender) {
        return new SDPAvsender(
                getSdpOrganisasjon(avsender.getVirksomhetsidentifikator()),
                avsender.getAvsenderidentifikator(),
                avsender.getFakturaReferanse());
    }

    private SDPOrganisasjon getSdpOrganisasjon(Identifikator identifikator) {
        return new SDPOrganisasjon(identifikator.getValue(), SDPIso6523Authority.fromValue(identifikator.getAuthority()));
    }
}
