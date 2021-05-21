package no.digdir.dpi.example;

import org.apache.commons.cli.CommandLine;
import org.springframework.stereotype.Component;

@Component
public class CommandLineConverter {

    public DpiExampleInput toDpiExampleInput(CommandLine commandLine) {
        return DpiExampleInput.builder()
                .files(new DpiExampleInput.Files(commandLine.getArgList()))
                .postkasseadresse(commandLine.getOptionValue("postkasseadresse", "Dummy"))
                .sertifikat(commandLine.getOptionValue("sertifikat"))
                .build();
    }
}
