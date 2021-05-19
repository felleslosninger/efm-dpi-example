package no.digdir.dpiexample;

import lombok.Builder;
import lombok.Value;

import java.io.File;

@Value
@Builder
public class DpiExampleOutput {

    File asic;
    String fingeravtrykk;
}
