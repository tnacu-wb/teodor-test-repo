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
import uk.co.whitbread.payments.model.booking.WebhookBookingRequest;
import uk.co.whitbread.payments.model.booking.WebhookBookingResponse;
import uk.co.whitbread.payments.properties.HotelBookingProperties;

import java.time.Duration;

@Slf4j
@Service
public class HotelBookingClient {

    private final WebClient webClient;

    private final HotelBookingProperties hotelBookingProperties;

    HotelBookingClient(@Qualifier("hotel-booking") WebClient webClient, HotelBookingProperties hotelBookingProperties) {
        this.webClient = webClient;
        this.hotelBookingProperties = hotelBookingProperties;
    }

    /**
     * Call Hotel Booking to make Bart booking.
     *
     * @param bookingId BART booking id
     */
    @Timed(description = "time taken to call Hotel Booking service make booking", value = "hotel.booking.make.booking")
    public Mono<WebhookBookingResponse> makeBooking(WebhookBookingRequest request, String bookingId) {
        return webClient
                .post()
                .uri(String.format(hotelBookingProperties.getMakeBookingEndpoint(), bookingId))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new BookingServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error encountered during make a booking for payment with id %s.", request.getPaymentId()), ErrorCodes.CONNECTION_ERROR)))
                .bodyToMono(WebhookBookingResponse.class)
                .doOnError(ex -> log.error("Booking service error captured for paymentId {} : {}",request.getPaymentId(),ex.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(3))
                        .doBeforeRetry(retrySignal -> log.warn("Retrying count {} for make booking for paymentId {} ",retrySignal.totalRetries(),request.getPaymentId())));
    }
}