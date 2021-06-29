package no.digdir.dpi.client.domain.messagetypes;

public interface MaskinportentokenHolder {

    String getMaskinportentoken();

    <T extends BusinessMessage<T>> BusinessMessage<T> setMaskinportentoken(String maskinportenToken);
}
