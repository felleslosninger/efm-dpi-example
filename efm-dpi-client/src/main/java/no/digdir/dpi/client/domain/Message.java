package no.digdir.dpi.client.domain;

import lombok.Data;

import java.net.URI;

@Data
public class Message {

    private String forretningsmelding;
    private URI downloadurl;
}
