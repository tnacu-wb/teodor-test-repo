package uk.co.whitbread.payments.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.payments.model.booking.WebhookBookingRequest;
import uk.co.whitbread.payments.model.booking.WebhookBookingResponse;
import uk.co.whitbread.payments.properties.HotelBookingProperties;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelBookingClientTest {

    @Mock
    private HotelBookingProperties hotelBookingProperties;
    private ObjectMapper mapper;
    private MockWebServer mockWebServer;
    private HotelBookingClient hotelBookingClient;
    private static final String BOOKING_BASE_ENDPOINT = "/booking/hotels/";
    private static final String SESSION_ID = "test_sessionId";
    private static final String BOOK = "/book";

    @BeforeEach
    public void setup() throws IOException {
        mapper = new ObjectMapper().findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient bookingWebClient = WebClient.create(String.format("http://localhost:%s",
                mockWebServer.getPort()));
        hotelBookingClient = new HotelBookingClient(bookingWebClient,hotelBookingProperties);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.close();
    }

    @Test
    void verifyMakeBookingSuccess() throws IOException {

       var request = WebhookBookingRequest.builder().paymentId("test-paymentID").build();
        when(hotelBookingProperties.getMakeBookingEndpoint()).thenReturn(BOOKING_BASE_ENDPOINT+SESSION_ID+BOOK);
        var webhookBookingResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/booking/webhookBookingResponse.json"), WebhookBookingResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(webhookBookingResponse))
                        .build()
        );

        WebhookBookingResponse actualWebhookBookingResponse = hotelBookingClient.makeBooking(request,SESSION_ID).block();
        assertNotNull(actualWebhookBookingResponse);
        assertEquals(SESSION_ID, actualWebhookBookingResponse.getSessionId());
    }
}
