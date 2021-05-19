package no.digdir.dpiexample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DpiExampleCommandLineRunner implements CommandLineRunner {
    private final Options options = new Options()
            .addOption(Option.builder("a")
                    .longOpt("avsender")
                    .hasArg()
                    .desc("Avsender. Typisk et organisasjonsnummer.")
                    .build())
            .addOption(Option.builder("m")
                    .longOpt("mottaker")
                    .hasArg()
                    .desc("Fødselsnummer til mottaker")
                    .build())
            .addOption(Option.builder("p")
                    .longOpt("postkasseadresse")
                    .hasArg()
                    .desc("Full postadresse.")
                    .build())
            .addOption(Option.builder("o")
                    .longOpt("orgnrpostkasse")
                    .hasArg()
                    .desc("Organisasjonsnummer til den som tar i mot digital post på vegne av innbygger.")
                    .build())
            .addOption(Option.builder("s")
                    .longOpt("sertifikat")
                    .required()
                    .hasArg()
                    .desc("Fil med PEM sertifikatet til mottaker-organisasjonen.")
                    .build())
            .addOption(Option.builder("t")
                    .longOpt("tittel")
                    .hasArg()
                    .desc("Tittel på forsendelsen")
                    .build());

    private final CommandLineConverter commandLineConverter;
    private final DpiExample dpiExample;

    @Override
    public void run(String... args) {
        try {
            CommandLine commandLine = getCommandLine(args);
            DpiExampleInput input = commandLineConverter.toDpiExampleInput(commandLine);
            DpiExampleOutput output = dpiExample.run(input);
            log.info("Fingeravtrykk: {}", output.getFingeravtrykk());
        } catch (ParseException e) {
            log.error(e.getLocalizedMessage());
            new HelpFormatter().printHelp("dpiexample", options);
            System.exit(1);
        }
    }

    private CommandLine getCommandLine(String... args) throws ParseException {
        return new DefaultParser().parse(options, args);
    }
}
