package no.digdir.dpi.client.internal;

import no.digdir.dpi.client.Blame;
import no.digdir.dpi.client.DpiException;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class DpiClientErrorHandlerImpl implements DpiClientErrorHandler {
    @Override
    public Mono<DpiException> apply(ClientResponse response) {
        return response.createException()
                .flatMap(ex -> Mono.error(new DpiException(
                        String.format("%s:%n%s", ex.getMessage(), ex.getResponseBodyAsString()),
                        ex,
                        getBlame(response))));
    }

    private Blame getBlame(ClientResponse response) {
        return response.statusCode().is5xxServerError() ? Blame.SERVER : Blame.CLIENT;
    }
}
