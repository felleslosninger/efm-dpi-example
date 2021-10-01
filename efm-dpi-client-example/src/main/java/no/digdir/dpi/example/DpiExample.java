package no.digdir.dpi.example;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.DpiClient;
import no.digdir.dpi.client.domain.GetMessagesInput;
import org.apache.commons.cli.CommandLine;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DpiExample {

    private final DpiClient dpiClient;
    private final ShipmentFactory shipmentFactory;
    private final GetDpiSendInput getDpiSendInput;

    public void run(CommandLine commandLine) {
        Operation operation = Operation.fromArg(commandLine.getArgList().get(0));

        switch (operation) {
            case SEND:
                sendMessage(commandLine);
                break;
            case GET:
                getMessages(commandLine);
                break;
            case MARK:
                markAsRead(commandLine);
                break;
        }
    }

    private void sendMessage(CommandLine commandLine) {
        DpiSendInput input = getDpiSendInput.getDpiSendInput(commandLine);
        dpiClient.sendMessage(shipmentFactory.getShipment(input));
    }

    private void getMessages(CommandLine commandLine) {
        String senderId = commandLine.getArgList().get(1);
        dpiClient.getMessages(new GetMessagesInput()
                        .setSenderId(senderId)
                )
                .subscribe(p -> log.debug("Received message: {}", p), e -> log.error("Get messages failed!", e));
    }

    private void markAsRead(CommandLine commandLine) {
        dpiClient.markAsRead(UUID.fromString(commandLine.getArgList().get(1)));
    }
}