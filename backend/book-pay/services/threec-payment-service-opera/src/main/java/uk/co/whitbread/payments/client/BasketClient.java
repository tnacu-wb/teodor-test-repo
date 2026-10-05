package uk.co.whitbread.payments.client;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.co.whitbread.payments.exception.BookingServiceException;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.model.booking.basket.BasketRequest;
import uk.co.whitbread.payments.model.booking.basket.BasketResponse;
import uk.co.whitbread.payments.properties.BasketBookingProperties;

import java.time.Duration;

@Slf4j
@Service
public class BasketClient {

    private final WebClient webClient;
    private final BasketBookingProperties basketBookingProperties;

    BasketClient(@Qualifier("basket-booking") WebClient webClient, BasketBookingProperties basketBookingProperties) {
        this.webClient = webClient;
        this.basketBookingProperties = basketBookingProperties;
    }

    /**
     * Call basket service to make an Opera booking.
     *
     * @param basketRequest request for Opera basket service
     */
    @Timed(description = "time taken to call Opera basket service confirm booking", value = "basket.booking.confirm.booking")
    public Mono<BasketResponse> makeBooking(BasketRequest basketRequest) {

        return webClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder
                                .scheme(basketBookingProperties.getSchema())
                                .port(basketBookingProperties.getPort())
                                .host(String.format(basketBookingProperties.getHost()))
                                .path(basketBookingProperties.getMakeBookingEndpoint())
                                .build(basketRequest.getReference()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(basketBookingProperties.getSecurityKey(), basketBookingProperties.getSecurityValue())
                .body(BodyInserters.fromValue(basketRequest))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(error -> {
                                    log.info(error);
                                    throw new BookingServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                                            String.format("Error encountered during make an Opera booking for payment with id %s.", basketRequest.getPaymentId()), ErrorCodes.CONNECTION_ERROR);
                                }))

                .bodyToMono(BasketResponse.class)
                .doOnError(ex -> log.error("Basket service error captured for paymentId {} : {}", basketRequest.getPaymentId(), ex.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(3))
                        .doBeforeRetry(retrySignal -> log.warn("Retrying count {} for make basket booking for paymentId {} ", retrySignal.totalRetries(), basketRequest.getPaymentId())));
    }
}