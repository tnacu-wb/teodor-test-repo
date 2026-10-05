package uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.DonationPackagesRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackageGroupsRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackagesRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.properties.PackagesOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.properties.PackagesProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelPackageException;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WiremocPackagesOhipClientTest {

  @Mock
  private PackagesOhipProperties packagesOhipProperties;
  @Mock
  private PackagesProperties packagesProperties;
  private WireMockServer wm;
  private String path;

  @BeforeAll
  void startWiremock() {
    wm = new WireMockServer(options().dynamicPort());
    wm.start();
    path = wm.baseUrl();
  }

  @BeforeEach
  void setUp() {
    wm.resetAll();

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));
  }

  @AfterAll
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipClient_ShouldReturnException() {
    final PackagesRequestOhipDto request = PackagesRequestOhipDto.builder().adults(1).build();
    WebClient webClient = WebClient.create(path);
    OhipPackagesClient ohipClient = new OhipPackagesClient(webClient, packagesOhipProperties,
        packagesProperties);

    //Act
    var response = ohipClient.getPackages(request, new LinkedMultiValueMap<>());
    assertThrows(HotelPackageException.class, () -> response.block());
  }

  @Test
  void testOhipClientGroups_ShouldReturnException() {
    final PackageGroupsRequestOhipDto group = PackageGroupsRequestOhipDto.builder().build();
    WebClient webClient = WebClient.create(path);
    OhipPackagesClient ohipClient = new OhipPackagesClient(webClient, packagesOhipProperties,
        packagesProperties);
    when(packagesOhipProperties.getPackageGroupsEndpoint()).thenReturn(
        "rtp/v1/hotels/{hotelId}/packageGroups");

    //Act
    var response = ohipClient.getPackageGroups(group);
    assertThrows(HotelPackageException.class, () -> response.block());
  }

  @Test
  void testOhipClientDonation_ShouldReturnException() {
    final DonationPackagesRequestOhipDto donation = DonationPackagesRequestOhipDto.builder()
        .hotelId("hotelId")
        .packageCodes(List.of("code"))
        .build();
    WebClient webClient = WebClient.create(path);
    OhipPackagesClient ohipClient = new OhipPackagesClient(webClient, packagesOhipProperties,
        packagesProperties);
    when(packagesOhipProperties.getPackagesEndpoint()).thenReturn("/rtp/v1/packages");
    when(packagesOhipProperties.getPackageGroupsEndpoint()).thenReturn(
        "rtp/v1/hotels/{hotelId}/packageGroups");
    //Act
    assertThrows(HotelPackageException.class,
        () -> ohipClient.getDonationPackagesDetails(donation));
  }
}
