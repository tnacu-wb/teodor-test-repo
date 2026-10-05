package uk.co.whitbread.content.infrastructure.rest.client.booking;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.InjectWireMock;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.booking.aem.adapter.BookingAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.BookingInformationRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.RateInformationRequestAemDto;

@ExtendWith(MockitoExtension.class)
@ConfigureWireMock(name = "wmBookingAemServer", port = 8080)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockBookingAemClientTest {

  @Mock
  private AemProperties aemProperties;
  @InjectWireMock("wmBookingAemServer")
  WireMockServer wm;

  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  private final BookingInformationRequestAemDto bookingRequest = BookingInformationRequestAemDto.builder()
      .country("UK").language("en").bookingFlowId("1").build();

  private final RateInformationRequestAemDto rateInformation = RateInformationRequestAemDto.builder()
      .country("UK").language("en").brand("1").hotelId("1").channel("BB").build();

  @BeforeEach
  void setUp() {
    wm.resetAll();
    lenient().when(aemProperties.getBookingInformationEndpoint()).thenReturn(
        "/{country}/{language}/content-service.booking-flow.detail/bookingId/{bookingFlowId}.json");
    lenient().when(aemProperties.getRateInformationEndpoint()).thenReturn(
        "{country}/{language}/content-service.rates.detail/site/leisure/brand/{brand}/hotelCode/{hotelId}.json");
    lenient().when(aemProperties.getRatesOverrideEndpoint()).thenReturn(
        "/{country}/{language}/content-service.rates-override.detail/site/leisure/brand/{brand}.json");
    lenient().when(aemProperties.getBbRateInformationEndpoint()).thenReturn(
        "{country}/{language}/content-service.rates.detail/site/business-booker/brand/{brand}.json");

    wm.stubFor(get("http://localhost:8080")
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));
  }

  @AfterEach
  void cleanUp() {
    wm.resetAll();
  }

  @Test
  void testAemClient_ShouldReturnException() {
    BookingAemClient aemClient = new BookingAemClient(aemProperties, webClient);
    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getBookingInformation(bookingRequest));
  }


  @Test
  void testAemClientRateInfo_ShouldReturnException() {
    BookingAemClient aemClient = new BookingAemClient(aemProperties, webClient);

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getRateInformationForBrand(rateInformation));
  }

  @Test
  void testAemClientInformationForHotel_ShouldReturnException() {
    BookingAemClient aemClient = new BookingAemClient(aemProperties, webClient);

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getRateInformationForHotel(rateInformation));
  }

  @Test
  void testAemClientRatesOverrideDetails_ShouldReturnException() {
    BookingAemClient aemClient = new BookingAemClient(aemProperties, webClient);

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getRatesOverrideDetails(rateInformation));
  }
}
