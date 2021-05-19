package no.digdir.dpiexample;

import org.apache.commons.cli.CommandLine;
import org.springframework.stereotype.Component;

@Component
public class CommandLineConverter {

    public DpiExampleInput toDpiExampleInput(CommandLine commandLine) {
        return DpiExampleInput.builder()
                .files(commandLine.getArgList())
                .avsender(commandLine.getOptionValue("avsender", "987654321"))
                .mottaker(commandLine.getOptionValue("mottaker", "12905299938"))
                .postkasseadresse(commandLine.getOptionValue("postkasseadresse", "Ola Nordmann, Langtoppilia 1, 6789 FJELL"))
                .orgnrPostkasse(commandLine.getOptionValue("orgnrpostkasse", "912345678"))
                .sertifikat(commandLine.getOptionValue("sertifikat"))
                .tittel(commandLine.getOptionValue("tittel", "Min flotte tittel"))
                .build();
    }
}
