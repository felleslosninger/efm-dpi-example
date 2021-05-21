package no.digdir.dpi.client.internal;

import no.difi.begrep.sdp.schema_v10.*;
import no.digdir.dpi.client.domain.Dokument;
import no.digdir.dpi.client.domain.Forsendelse;
import no.digdir.dpi.client.domain.sbd.Avsender;
import no.digdir.dpi.client.domain.sbd.Identifikator;
import no.digdir.dpi.client.domain.sbd.Mottaker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SDPBuilder {

    public SDPManifest createManifest(Forsendelse forsendelse) {
        return new SDPManifest(
                getSdpMottaker(forsendelse),
                sdpAvsender(getAvsender(forsendelse)),
                getHoveddokument(forsendelse),
                getSdpVedlegg(forsendelse), null);
    }

    private Avsender getAvsender(Forsendelse forsendelse) {
        return forsendelse.getStandardBusinessDocument().getDigitalpost().getAvsender();
    }

    private SDPDokument getHoveddokument(Forsendelse forsendelse) {
        return sdpDokument(forsendelse.getDokumentpakke().getHoveddokument(), forsendelse.getSpraakkode());
    }

    private List<SDPDokument> getSdpVedlegg(Forsendelse forsendelse) {
        return forsendelse.getDokumentpakke().getVedlegg()
                .stream()
                .map(dokument -> sdpDokument(dokument, forsendelse.getSpraakkode()))
                .collect(Collectors.toList());
    }

    private SDPDokument sdpDokument(final Dokument dokument, final String spraakkode) {
        return new SDPDokument(
                getSdpTittel(dokument, spraakkode),
                getSdpDokumentData(dokument),
                dokument.getFilnavn(),
                dokument.getMimeType());
    }

    private SDPDokumentData getSdpDokumentData(Dokument dokument) {
        return Optional.ofNullable(dokument.getMetadataDocument())
                .map(d -> new SDPDokumentData(d.getFileName(), d.getMimeType()))
                .orElse(null);
    }

    private SDPTittel getSdpTittel(Dokument dokument, String spraakkode) {
        return dokument.getTittel() != null ? new SDPTittel(dokument.getTittel(), spraakkode) : null;
    }

    private SDPMottaker getSdpMottaker(Forsendelse forsendelse) {
        return new SDPMottaker(getSdpPerson(forsendelse));
    }

    private SDPPerson getSdpPerson(Forsendelse forsendelse) {
        Mottaker mottaker = forsendelse.getStandardBusinessDocument().getDigitalpost().getMottaker();
        return new SDPPerson(mottaker.getPersonidentifikator().getValue(), forsendelse.getPostkasseadresse());
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
