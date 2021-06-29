package no.digdir.dpi.client.domain;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MessageStatus {

    private ReceiptStatus status;
    private OffsetDateTime timestamp;
}
