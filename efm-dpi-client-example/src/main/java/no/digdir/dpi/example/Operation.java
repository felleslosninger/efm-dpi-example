package no.digdir.dpi.example;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Operation {

    SEND("send"),
    GET("get"),
    MARK("mark");

    @Getter
    private final String arg;

    Operation(String arg) {
        this.arg = arg;
    }

    public static Operation fromArg(String arg) {
        return Arrays.stream(Operation.values())
                .filter(p -> p.arg.equals(arg))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unknown operation. Expected one of %s", Arrays.stream(Operation.values())
                                .map(Operation::getArg)
                                .collect(Collectors.joining(", ")))));
    }
}
