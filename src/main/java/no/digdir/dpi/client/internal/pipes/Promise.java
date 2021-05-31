package no.digdir.dpi.client.internal.pipes;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

@Slf4j
public class Promise<T> {

    private final CompletableFuture<Void> completableFuture;
    private final AtomicReference<PromiseStatus> status;
    private final AtomicReference<T> resolved = new AtomicReference<>();
    private final AtomicReference<Throwable> rejected = new AtomicReference<>();

    public Promise(BiConsumer<Resolve<T>, Reject> action) {
        this(action, Executors.newSingleThreadExecutor());
    }

    public Promise(BiConsumer<Resolve<T>, Reject> action, Executor executor) {
        this.status = new AtomicReference<>(PromiseStatus.PENDING);
        this.completableFuture = CompletableFuture.runAsync(() -> action.accept(this::resolve, this::reject), executor)
                .whenComplete((v, t) -> {
                    if (status.get() == PromiseStatus.PENDING) {
                        String message = "Promise completed without being resolved or rejected!";
                        log.error(message);
                        reject(t != null ? new Exception(message, t) : new Exception(message));
                    }
                });
    }

    public void resolve(T t) {
        if (status.compareAndSet(PromiseStatus.PENDING, PromiseStatus.FULLFILLED)) {
            resolved.set(t);
        }
    }

    public void reject(Throwable t) {
        if (status.compareAndSet(PromiseStatus.PENDING, PromiseStatus.REJECTED)) {
            rejected.set(t);
        }
    }

    public T await() {
        try {
            completableFuture.get();
        } catch (InterruptedException e) {
            log.warn("Thread interrupted", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new Exception("Promise catched exception that was not rejected!", e);
        }

        if (status.get() == PromiseStatus.PENDING) {
            reject(new Exception("Promise completed without being resolved or rejected!"));
        }

        if (status.get() == PromiseStatus.FULLFILLED) {
            return resolved.get();
        }

        throw new Exception("Promise was rejected", rejected.get());
    }

    private static class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }

        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}