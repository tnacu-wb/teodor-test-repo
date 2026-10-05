package uk.co.whitbread.hotel.info.service.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Swallows anything thrown from wrapped Future
 */
@Slf4j
public class OptionalFuture<T> {

    private CompletableFuture<T> future;

    public OptionalFuture(CompletableFuture<T> future) {
        this.future = future;
    }

    public Optional<T> finishAndGet() {
        try {
            return Optional.ofNullable(future.get());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Problem getting future={}", future.getClass().getName(), e);
            return Optional.empty();
        }
    }
}
