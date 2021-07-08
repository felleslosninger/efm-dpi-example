package no.digdir.dpi.client.domain.messagetypes;

import lombok.Getter;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum MessageType {

    DIGITAL("digital", Digital.class),
    UTSKRIFT("utskrift", Utskrift.class),
    FEIL("feil", Feil.class),
    LEVERINGSKVITTERING("leveringskvittering", Leveringskvittering.class),
    AAPNINGSKVITTERING("aapningskvittering", Aapningskvittering.class),
    VARSLINGFEILETKVITTERING("varslingfeiletkvittering", Varslingfeiletkvittering.class),
    MOTTAKSKVITTERING("mottakskvittering", Mottakskvittering.class),
    RETURPOSTKVITTERING("returpostkvittering", Returpostkvittering.class),
    FLYTTET("flyttet", Flyttet.class);

    private final String type;
    private final Class<? extends BusinessMessage> clazz;
    private final URI schemaUri;

    MessageType(String type, Class<? extends BusinessMessage> clazz) {
        this.type = type;
        this.clazz = clazz;
        this.schemaUri = URI.create(String.format("https://docs.digdir.no/schemas/dpi/innbyggerpost_dpi_%s_1_0.schema.json", type));
    }

    public static MessageType fromType(String type) {
        return Arrays.stream(MessageType.values()).filter(p -> p.getType().equalsIgnoreCase(type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown MessageType = %s. Expecting one of %s",
                        type,
                        Arrays.stream(values()).map(MessageType::getType).collect(Collectors.joining(",")))));
    }
}
