package no.digdir.dpi.client.internal.pipes;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.PipedOutputStream;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class Plumber {

    private final TaskExecutor taskExecutor;

    public Pipe pipe(String description, Consumer<PipedOutputStream> consumer, Reject reject) {
        return Pipe.of(taskExecutor, description, consumer, reject);
    }
}
