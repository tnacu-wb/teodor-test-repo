package uk.co.whitbread.hotel.info.service.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class OptionalFutureTest {

    @Test
    public void finishAndGet() {
        AtomicInteger counter = new AtomicInteger();
        counter.incrementAndGet();
        final OptionalFuture<String> stringOptionalFuture = new OptionalFuture<>(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(300);
                counter.incrementAndGet();
                return "Testing 123";
            } catch (InterruptedException e) {
                return "Error";
            }
        }));
        Assertions.assertThat(counter.get()).isEqualTo(1);
        final Optional<String> stringOptional = stringOptionalFuture.finishAndGet();
        Assertions.assertThat(counter.get()).isEqualTo(2);
        Assertions.assertThat(stringOptional.get()).isEqualTo("Testing 123");
    }

    @Test
    public void finishAndGetShouldDelayResult() {
        AtomicInteger counter = new AtomicInteger();
        counter.incrementAndGet();
        final OptionalFuture<String> stringOptionalFuture = new OptionalFuture<>(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(300);
                counter.incrementAndGet();
                return "Testing 123";
            } catch (InterruptedException e) {
                return "Error";
            }
        }));
        Assertions.assertThat(counter.get()).isEqualTo(1);
        Assertions.assertThat(counter.get()).isEqualTo(1);
        final Optional<String> stringOptional = stringOptionalFuture.finishAndGet();
        Assertions.assertThat(stringOptional.get()).isEqualTo("Testing 123");
    }

    @Test
    public void finishAndGetShouldReturnOptionalEmptyInCaseOfException() {

        final OptionalFuture<String> stringOptionalFuture = new OptionalFuture<>(CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Unexpected Error");
        }));
        final Optional<String> optional = stringOptionalFuture.finishAndGet();
        Assertions.assertThat(optional).isEqualTo(Optional.empty());
    }
}