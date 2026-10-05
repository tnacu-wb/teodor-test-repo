package uk.co.whitbread.shared.auth.webclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.PrematureCloseException;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;
import uk.co.whitbread.shared.auth.exception.RetriesExhaustedException;

class WebClientRetryHandlerTest {

    private WebClientRetryHandler webClientRetryHandler;
    private PrematureCloseException prematureCloseException;

    @BeforeEach
    void setUp() {
        webClientRetryHandler = new WebClientRetryHandler();
        prematureCloseException = PrematureCloseException.TEST_EXCEPTION;
    }

    @Test
    void getPrematureCloseExceptionRetrySpec_returnsNonNullSpec() {
        Retry spec = webClientRetryHandler.getPrematureCloseExceptionRetrySpec();
        assertNotNull(spec);
    }

    @Test
    void retrySpec_retriesOnDirectPrematureCloseException() {
        Retry spec = webClientRetryHandler.getPrematureCloseExceptionRetrySpec();

        StepVerifier.withVirtualTime(() ->
                Mono.error(prematureCloseException)
                    .retryWhen(spec)
            )
            .thenAwait(Duration.ofSeconds(30))
            .expectErrorSatisfies(e -> assertInstanceOf(RetriesExhaustedException.class, e))
            .verify();
    }

    @Test
    void retrySpec_retriesOnWrappedPrematureCloseException() {
        Retry spec = webClientRetryHandler.getPrematureCloseExceptionRetrySpec();

        WebClientRequestException wrappedException = new WebClientRequestException(
                prematureCloseException,
                org.springframework.http.HttpMethod.GET,
                URI.create("https://example.com"),
                org.springframework.http.HttpHeaders.EMPTY
        );

        StepVerifier.withVirtualTime(() ->
                Mono.error(wrappedException)
                    .retryWhen(spec)
            )
            .thenAwait(Duration.ofSeconds(30))
            .expectErrorSatisfies(e -> assertInstanceOf(RetriesExhaustedException.class, e))
            .verify();
    }

    @Test
    void retrySpec_doesNotRetryOnOtherExceptions() {
        Retry spec = webClientRetryHandler.getPrematureCloseExceptionRetrySpec();

        StepVerifier.create(
                Mono.error(new RuntimeException("some other error"))
                    .retryWhen(spec)
            )
            .expectErrorSatisfies(e -> assertInstanceOf(RuntimeException.class, e))
            .verify();
    }

     @Test
     void retrySpec_throwsRetriesExhaustedExceptionAfterMaxRetries() {
         Retry spec = webClientRetryHandler.getPrematureCloseExceptionRetrySpec();
         AtomicInteger attempts = new AtomicInteger();

         StepVerifier.withVirtualTime(() ->
                 Mono.<Void>error(prematureCloseException)
                     .doOnSubscribe(s -> attempts.incrementAndGet())
                     .retryWhen(spec)
             )
             .thenAwait(Duration.ofSeconds(30))
             .expectErrorSatisfies(e -> {
                 assertInstanceOf(RetriesExhaustedException.class, e);
                 assertEquals(4, attempts.get()); // 1 initial + 3 retries
             })
             .verify();
     }

     // Tests for getTransientAndInternalServerErrorRetrySpec()

     @Test
     void getTransientAndInternalServerErrorRetrySpec_returnsNonNullSpec() {
         Retry spec = webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec();
         assertNotNull(spec);
     }

     @Test
     void transientAndInternalServerErrorRetrySpec_retriesOnWebClientRequestException() {
         Retry spec = webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec();

         WebClientRequestException networkException = new WebClientRequestException(
                 new RuntimeException("Connection reset by peer"),
                 org.springframework.http.HttpMethod.GET,
                 URI.create("https://example.com"),
                 org.springframework.http.HttpHeaders.EMPTY
         );

         StepVerifier.withVirtualTime(() ->
                 Mono.error(networkException)
                     .retryWhen(spec)
             )
             .thenAwait(Duration.ofSeconds(30))
             .expectErrorSatisfies(e -> assertInstanceOf(RetriesExhaustedException.class, e))
             .verify();
     }

     @Test
     void transientAndInternalServerErrorRetrySpec_retriesOnDirectPrematureCloseException() {
         Retry spec = webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec();

         StepVerifier.withVirtualTime(() ->
                 Mono.error(prematureCloseException)
                     .retryWhen(spec)
             )
             .thenAwait(Duration.ofSeconds(30))
             .expectErrorSatisfies(e -> assertInstanceOf(RetriesExhaustedException.class, e))
             .verify();
     }

     @Test
     void transientAndInternalServerErrorRetrySpec_retriesOnWrappedPrematureCloseException() {
         Retry spec = webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec();

         WebClientRequestException wrappedException = new WebClientRequestException(
                 prematureCloseException,
                 org.springframework.http.HttpMethod.GET,
                 URI.create("https://example.com"),
                 org.springframework.http.HttpHeaders.EMPTY
         );

         StepVerifier.withVirtualTime(() ->
                 Mono.error(wrappedException)
                     .retryWhen(spec)
             )
             .thenAwait(Duration.ofSeconds(30))
             .expectErrorSatisfies(e -> assertInstanceOf(RetriesExhaustedException.class, e))
             .verify();
     }

     @Test
     void transientAndInternalServerErrorRetrySpec_retriesOnInternalServerError() {
         Retry spec = webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec();

         StepVerifier.withVirtualTime(() ->
                 Mono.error(webClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR))
                     .retryWhen(spec)
             )
             .thenAwait(Duration.ofSeconds(30))
             .expectErrorSatisfies(e -> assertInstanceOf(RetriesExhaustedException.class, e))
             .verify();
     }

     @Test
     void transientAndInternalServerErrorRetrySpec_doesNotRetryOnNonInternalServerError() {
         Retry spec = webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec();

         StepVerifier.create(
                 Mono.error(webClientResponseException(HttpStatus.BAD_GATEWAY))
                     .retryWhen(spec)
             )
             .expectErrorSatisfies(e -> {
                 WebClientResponseException ex = assertInstanceOf(WebClientResponseException.class, e);
                 assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
             })
             .verify();
     }

     @Test
     void transientAndInternalServerErrorRetrySpec_doesNotRetryOnRuntimeException() {
         Retry spec = webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec();

         StepVerifier.create(
                 Mono.error(new RuntimeException("some other error"))
                     .retryWhen(spec)
             )
             .expectErrorSatisfies(e -> assertInstanceOf(RuntimeException.class, e))
             .verify();
     }

     @Test
     void transientAndInternalServerErrorRetrySpec_throwsRetriesExhaustedExceptionAfterMaxRetries() {
         Retry spec = webClientRetryHandler.getTransientAndInternalServerErrorRetrySpec();
         AtomicInteger attempts = new AtomicInteger();

         WebClientRequestException networkException = new WebClientRequestException(
                 new RuntimeException("Connection reset by peer"),
                 org.springframework.http.HttpMethod.GET,
                 URI.create("https://example.com"),
                 org.springframework.http.HttpHeaders.EMPTY
         );

         StepVerifier.withVirtualTime(() ->
                 Mono.<Void>error(networkException)
                     .doOnSubscribe(s -> attempts.incrementAndGet())
                     .retryWhen(spec)
             )
             .thenAwait(Duration.ofSeconds(30))
             .expectErrorSatisfies(e -> {
                 assertInstanceOf(RetriesExhaustedException.class, e);
                 assertEquals(4, attempts.get()); // 1 initial + 3 retries
             })
             .verify();
     }

    private WebClientResponseException webClientResponseException(HttpStatus status) {
        return WebClientResponseException.create(
                status.value(),
                status.getReasonPhrase(),
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );
    }
}
