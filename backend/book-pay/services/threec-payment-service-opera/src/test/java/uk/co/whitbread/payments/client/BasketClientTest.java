package uk.co.whitbread.payments.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.payments.exception.BookingServiceException;
import uk.co.whitbread.payments.model.booking.basket.BasketError;
import uk.co.whitbread.payments.model.booking.basket.BasketRequest;
import uk.co.whitbread.payments.model.booking.basket.BasketResponse;
import uk.co.whitbread.payments.properties.BasketBookingProperties;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BasketClientTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private MockWebServer mockWebServer;
    private BasketClient basketClient;
    private static final String BOOKING_BASE_ENDPOINT = "/v1/baskets";

    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        var bookingWebClient = WebClient.create();
        var basketBookingProperties = new BasketBookingProperties();
        basketBookingProperties.setMakeBookingEndpoint(BOOKING_BASE_ENDPOINT);
        basketBookingProperties.setSecurityKey("X-WHIT-AUTH");
        basketBookingProperties.setSecurityValue("djdjdjhdhdjdjdh");
        basketBookingProperties.setSchema("http");
        basketBookingProperties.setPort(mockWebServer.getPort());
        basketBookingProperties.setHost("localhost");
        basketClient = new BasketClient(bookingWebClient, basketBookingProperties);
    }

    @AfterEach
    void tearDown() {
        mockWebServer.close();
    }

    @Test
    void verifyMakeBookingSuccess() throws IOException {

        var basketResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/booking/basketBookingResponse.json"), BasketResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(basketResponse))
                        .build()
        );

        var request = BasketRequest.builder()
                .reference("ref-1")
                .paymentId("payment-id")
                .paymentStatus("APPROVED")
                .bookingReference("ref-1")
                .countryCode("GB")
                .language("en")
                .firstName("Chris")
                .lastName("Tester")
                .channel("WEB")
                .last4Digits("1234")
                .cardSchemeId("VI")
                .token("token")
                .expiry("12/23")
                .fraudCheckDecision("FAILED")
                .build();

        var actualWebhookBookingResponse = basketClient.makeBooking(request).block();
        assertNotNull(actualWebhookBookingResponse);
        assertEquals("ref-1", actualWebhookBookingResponse.getReference());
    }

    @Test
    void verifyMakeBookingFailure() throws IOException {

        var basketErrorResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/booking/basketBookingFailureResponse.json"), BasketError.class);

        var errorResponse = new MockResponse.Builder()
                .code(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(mapper.writeValueAsString(basketErrorResponse))
                .build();

        mockWebServer.enqueue(errorResponse);
        mockWebServer.enqueue(errorResponse);
        mockWebServer.enqueue(errorResponse);
        mockWebServer.enqueue(errorResponse);

        var request = BasketRequest.builder()
                .paymentId("payment-id")
                .paymentStatus("APPROVED")
                .countryCode("GB")
                .language("en")
                .firstName("Chris")
                .lastName("Tester")
                .channel("WEB")
                .last4Digits("1234")
                .cardSchemeId("VI")
                .token("token")
                .expiry("12/23")
                .fraudCheckDecision("FAILED")
                .build();

        var exception = assertThrows(Exception.class, () -> basketClient.makeBooking(request).block());

        assertThat(exception.getCause()).isInstanceOf(BookingServiceException.class);
        var bookingException = (BookingServiceException) exception.getCause();
        assertThat(bookingException.getReason()).isEqualTo("Error encountered during make an Opera booking for payment with id payment-id.");
    }
}
