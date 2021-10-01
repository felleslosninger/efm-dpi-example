package no.digdir.dpi.client.domain;

import lombok.Data;

@Data
public class GetMessagesInput {

    private String senderId;
    private String channel;
}
