package no.digdir.dpi.example;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.core.io.Resource;

import java.util.List;

@Value
@Builder
public class DpiExampleInput {

    @NonNull Resource standardBusinessDocument;
    @NonNull Resource hoveddokument;
    @NonNull List<Resource> vedlegg;
    @NonNull String postkasseadresse;
    @NonNull Resource sertifikat;
}
