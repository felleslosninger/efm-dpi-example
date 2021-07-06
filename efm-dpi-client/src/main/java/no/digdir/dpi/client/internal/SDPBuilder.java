package no.digdir.dpi.client.internal;

import no.difi.begrep.sdp.schema_v10.*;
import no.digdir.dpi.client.domain.Document;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.messagetypes.BusinessMessage;
import no.digdir.dpi.client.domain.messagetypes.Digital;
import no.digdir.dpi.client.domain.sbd.Avsender;
import no.digdir.dpi.client.domain.sbd.Identifikator;
import no.digdir.dpi.client.domain.sbd.Personmottaker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SDPBuilder {

    public SDPManifest createManifest(Shipment shipment) {
        return SDPManifest.builder()
                .withMottaker(getMottaker(shipment))
                .withAvsender(getAvsender(shipment))
                .withHoveddokument(getHoveddokument(shipment))
                .withVedleggs(getVedlegg(shipment))
                .build();
    }

    private SDPAvsender getAvsender(Shipment shipment) {
        return getAvsender(shipment.getStandardBusinessDocument().getBusinessMessage().getAvsender());
    }

    private SDPDokument getHoveddokument(Shipment shipment) {
        return sdpDokument(shipment.getParcel().getMainDocument(), shipment.getLanguage());
    }

    private List<SDPDokument> getVedlegg(Shipment shipment) {
        return shipment.getParcel().getAttachments()
                .stream()
                .map(document -> sdpDokument(document, shipment.getLanguage()))
                .collect(Collectors.toList());
    }

    private SDPDokument sdpDokument(final Document document, final String spraakkode) {
        return SDPDokument.builder()
                .withTittel(getTittel(document, spraakkode))
                .withData(getDokumentData(document))
                .withHref(document.getFilename())
                .withMime(document.getMimeType())
                .build();
    }

    private SDPDokumentData getDokumentData(Document document) {
        return Optional.ofNullable(document.getMetadataDocument())
                .map(d -> SDPDokumentData.builder()
                        .withHref(d.getFilename())
                        .withMime(d.getMimeType())
                        .build())
                .orElse(null);
    }

    private SDPTittel getTittel(Document document, String spraakkode) {
        return document.getTitle() != null ? SDPTittel.builder()
                .withValue(document.getTitle())
                .withLang(spraakkode)
                .build() : null;
    }

    private SDPMottaker getMottaker(Shipment shipment) {
        return SDPMottaker.builder()
                .withPerson(getPerson(shipment))
                .build();
    }

    private SDPPerson getPerson(Shipment shipment) {
        BusinessMessage<? extends BusinessMessage<?>> message = shipment.getStandardBusinessDocument().getBusinessMessage();

        if (message instanceof Digital) {
            Digital digital = (Digital) message;
            Personmottaker mottaker = digital.getMottaker();

            return SDPPerson.builder()
                    .withPostkasseadresse(mottaker.getPostkasseadresse())
                    .build();
        }

        return null;
    }

    private SDPAvsender getAvsender(Avsender avsender) {
        return SDPAvsender.builder()
                .withOrganisasjon(getOrganisasjon(avsender.getVirksomhetsidentifikator()))
                .withAvsenderidentifikator(avsender.getAvsenderidentifikator())
                .withFakturaReferanse(avsender.getFakturaReferanse())
                .build();
    }

    private SDPOrganisasjon getOrganisasjon(Identifikator identifikator) {
        return SDPOrganisasjon.builder()
                .withValue(identifikator.getValue())
                .withAuthority(SDPIso6523Authority.fromValue(identifikator.getAuthority()))
                .build();
    }

}
