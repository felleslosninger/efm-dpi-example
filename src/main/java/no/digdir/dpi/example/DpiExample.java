package no.digdir.dpi.example;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.DpiClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DpiExample {

    private final DpiClient dpiClient;
    private final ForsendelseFactory forsendelseFactory;

    @SneakyThrows
    public void run(DpiExampleInput input) {
        dpiClient.send(forsendelseFactory.getForsendelse(input));
    }
}