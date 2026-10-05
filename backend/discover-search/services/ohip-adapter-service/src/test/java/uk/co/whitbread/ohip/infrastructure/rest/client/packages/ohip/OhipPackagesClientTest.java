package uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackagesRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.properties.PackagesOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.properties.PackagesProperties;

@ExtendWith(MockitoExtension.class)
class OhipPackagesClientTest {

  public static final String MOCK_PACKAGES_RESPONSE_JSON = "classpath:__files/ohip_packageCodesList_object.json";
  public static final String HOTEL_ID = "TEST";
  public static final String START_DATE = "2022-12-30";
  public static final String END_DATE = "2022-12-31";
  public static final int ADULTS = 2;
  public static final int CHILDREN = 1;

  public static final String PACKAGE_CODE = "BKFSTTEST";
  private static final String PACKAGE_DESC = "Premier Inn Breakfast";

  @Mock
  private PackagesOhipProperties packagesOhipProperties;

  @Mock
  private PackagesProperties packagesProperties;

  private OhipPackagesClient ohipPackagesClient;

  private MockWebServer mockServer;

  @BeforeEach
  public void init() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();

    String rootUrl = mockServer.url("/hotels/packages").toString();
    File file = ResourceUtils.getFile(MOCK_PACKAGES_RESPONSE_JSON);

    mockBackendEndpoint(200, new String(Files.readAllBytes(file.toPath())));

    when(packagesProperties.getFetchInstructions())
        .thenReturn(List.of("Header", "CalculatedPrice", "Items"));

    ohipPackagesClient = new OhipPackagesClient(WebClient.create(rootUrl), packagesOhipProperties,
        packagesProperties);
  }

  @AfterEach
  public void tearDown() throws IOException {
    mockServer.shutdown();
  }

  @Test
  void getPackages__shouldReturnOk() {

    // Arrange

    // Act
    PackagesResponseOhipDto packagesResponse = ohipPackagesClient.getPackages(getRequest(),
        new LinkedMultiValueMap<>()).block();

    // Assert
    assertNotNull(packagesResponse);

  }


  private static PackagesRequestOhipDto getRequest() {
    return PackagesRequestOhipDto.builder()
        .hotelId(HOTEL_ID)
        .startDate(START_DATE)
        .endDate(END_DATE)
        .adults(ADULTS)
        .children(CHILDREN)
        .build();
  }

  private void mockBackendEndpoint(int responseCode, String body) {
    MockResponse mockResponse = new MockResponse().setResponseCode(responseCode)
        .setBody(body)
        .addHeader("Content-Type", "application/json");
    mockServer.enqueue(mockResponse);
  }

}
