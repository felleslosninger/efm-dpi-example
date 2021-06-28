package no.digdir.dpi.client.domain.sbd;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum MessageType {

    DIGITAL("digital", Digital.class, URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_digital_1_0.schema.json")),
    UTSKRIFT("utskrift", Utskrift.class, URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_utskrift_1_0.schema.json"));

//            schemaMap.put("leveringskvittering", schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_leveringskvittering_1_0.schema.json")));
//        schemaMap.put("aapningskvittering", schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_aapningskvittering_1_0.schema.json")));
//        schemaMap.put("varslingfeiletkvittering", schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_varslingfeiletkvittering_1_0.schema.json")));
//        schemaMap.put("mottakskvittering", schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_mottakskvittering_1_0.schema.json")));
//        schemaMap.put("returpostkvittering", schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_returpostkvittering_1_0.schema.json")));
//        schemaMap.put("feil", schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_feil_1_0.schema.json")));
//        schemaMap.put("flyttet", schemaStore.loadSchema(URI.create("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_flyttet_1_0.schema.json")));

    private final String type;
    private final Class<? extends Message<?>> clazz;
    private final URI schemaUri;

    public static MessageType fromType(String type) {
        return Arrays.stream(MessageType.values()).filter(p -> p.getType().equalsIgnoreCase(type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown MessageType = %s. Expecting one of %s",
                        type,
                        Arrays.stream(values()).map(MessageType::getType).collect(Collectors.joining(",")))));
    }
}
