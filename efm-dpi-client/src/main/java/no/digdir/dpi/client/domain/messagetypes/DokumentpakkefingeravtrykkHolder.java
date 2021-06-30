package no.digdir.dpi.client.domain.messagetypes;

import no.digdir.dpi.client.domain.sbd.Dokumentpakkefingeravtrykk;

public interface DokumentpakkefingeravtrykkHolder {

    Dokumentpakkefingeravtrykk getDokumentpakkefingeravtrykk();

    <T extends BusinessMessage<T>> BusinessMessage<T> setDokumentpakkefingeravtrykk(Dokumentpakkefingeravtrykk dokumentpakkefingeravtrykk);
}
