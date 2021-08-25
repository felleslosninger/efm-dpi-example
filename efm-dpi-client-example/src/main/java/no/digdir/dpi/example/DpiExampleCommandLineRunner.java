package no.digdir.dpi.example;

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
            .addOption(Option.builder("c")
                    .longOpt("certificate")
                    .required()
                    .hasArg()
                    .desc("The PEM business certificate of the receiving organisation.")
                    .build());

    private final CommandLineConverter commandLineConverter;
    private final DpiExample dpiExample;

    @Override
    public void run(String... args) {
        try {
            CommandLine commandLine = getCommandLine(args);
            DpiExampleInput input = commandLineConverter.toDpiExampleInput(commandLine);
            dpiExample.run(input);
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
