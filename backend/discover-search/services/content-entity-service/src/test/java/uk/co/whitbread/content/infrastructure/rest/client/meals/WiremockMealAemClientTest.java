package uk.co.whitbread.content.infrastructure.rest.client.meals;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
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
import uk.co.whitbread.content.infrastructure.rest.client.meals.adapter.MealsAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.out.MealsRequestAemDto;

@ExtendWith(MockitoExtension.class)
@ConfigureWireMock(name = "wmMealAemServer", port = 8080)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockMealAemClientTest {

  @Mock
  private AemProperties aemProperties;
  @InjectWireMock("wmMealAemServer")
  WireMockServer wm;
  String path = "http://localhost:8080";

  MealsRequestAemDto request = MealsRequestAemDto.builder().hotelId("1")
      .country("UK").language("de").build();

  @BeforeEach
  void setUp() {
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
    WebClient webClient = WebClient.create(path);
    MealsAemClient aemClient = new MealsAemClient(webClient, aemProperties);
    when(aemProperties.getUpsellItemsEndpoint()).thenReturn(
        "/{country}/{language}/hoteldirectory/{initialCharacter}/{hotelId}.upsellitems.data");

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getMealsInfo(request));
  }

}
