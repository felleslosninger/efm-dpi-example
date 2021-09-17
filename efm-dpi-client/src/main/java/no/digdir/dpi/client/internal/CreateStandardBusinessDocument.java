package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.difi.meldingsutveksling.domain.sbdh.*;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.domain.messagetypes.Direction;
import no.digdir.dpi.client.domain.messagetypes.MessageType;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class CreateStandardBusinessDocument {

    private final Clock clock;

    public StandardBusinessDocument createStandardBusinessDocument(Shipment shipment) {
        return new StandardBusinessDocument()
                .setStandardBusinessDocumentHeader(getStandardBusinessDocumentHeader(shipment))
                .setAny(shipment.getBusinessMessage());
    }

    private StandardBusinessDocumentHeader getStandardBusinessDocumentHeader(Shipment shipment) {
        MessageType outgoingMessageType = MessageType.fromClass(shipment.getBusinessMessage(), Direction.OUTGOING);

        return new StandardBusinessDocumentHeader()
                .setHeaderVersion("1.0")
                .addSender(new Partner().setIdentifier(shipment.getSenderOrganizationIdentifier()))
                .addReceiver(new Partner().setIdentifier(shipment.getReceiverOrganizationIdentifier()))
                .setDocumentIdentification(new DocumentIdentification()
                        .setInstanceIdentifier(shipment.getMessageId())
                        .setStandard(outgoingMessageType.getStandard())
                        .setType(outgoingMessageType.getType())
                        .setTypeVersion("1.0")
                        .setCreationDateAndTime(OffsetDateTime.now(clock)))
                .setBusinessScope(new BusinessScope()
                        .addScope(new Scope()
                                .setType(ScopeType.CONVERSATION_ID.getFullname())
                                .setInstanceIdentifier(shipment.getConversationId())
                                .setIdentifier(outgoingMessageType.getProcess())
                                .addScopeInformation(new CorrelationInformation()
                                        .setExpectedResponseDateTime(shipment.getExpectedResponseDateTime())
                                )
                        )
                );
    }
}
