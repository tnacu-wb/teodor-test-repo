package uk.co.whitbread.payapp.infrastructure.rest.client.worldline;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardDetails;
import uk.co.whitbread.payapp.domain.model.in.LookupName;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.exceptions.WorldlineResponseException;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.SubmitApplicationRequestDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.TrustedPartnerCredentialsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppCompanyDetailsUpdateRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppContactDetailsUpdateRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppCancelRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppInitRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineHeadersDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WiremockWorldlineClientTest {

  @Mock
  private WorldlineProperties worldlineProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final JsonMapper jsonMapper = JsonMapper.builder()
      .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
      .build();
  private final WebClient worldlineWebClient = WebClient.builder()
      .baseUrl(path)
      .codecs(configurer -> {
        configurer.registerDefaults(false);
        configurer.customCodecs()
            .register(new JacksonJsonEncoder(jsonMapper, MediaType.APPLICATION_JSON));
        configurer.customCodecs()
            .register(new JacksonJsonDecoder(jsonMapper, MediaType.APPLICATION_JSON));
      })
      .build();

  @BeforeEach
  void setUp() {
    when(worldlineProperties.getAppInitEndpoint()).thenReturn("/api/v1/app/init");
    when(worldlineProperties.getApplicationDetailsEndpoint()).thenReturn(
        "/api/v1/app/{applicationGUID}/details");
    when(worldlineProperties.getAppContactDetailsEndpoint()).thenReturn(
        "/api/v1/app/contactDetails/{applicationGUID}");
    when(worldlineProperties.getAppCompanyDetailsEndpoint()).thenReturn(
        "/api/v1/app/companyDetails/{applicationGUID}");
    when(worldlineProperties.getAppLookupEndpoint()).thenReturn(
        "/api/v1/app/lookup/{lookupName}");
    when(worldlineProperties.getAppCancelEndpoint()).thenReturn(
        "/api/v1/app/cancel/{applicationGUID}");
    when(worldlineProperties.getAppCompanyDetailsLookupEndpoint()).thenReturn(
        "/api/v1/app/companyDetailsLookup/{companyRegistrationNumber}");
    when(worldlineProperties.getAppCardDeleteEndpoint()).thenReturn(
        "/api/v1/app/{applicationGUID}/card/{cardGUID}");
    when(worldlineProperties.getAppCardAddEndpoint()).thenReturn(
        "/api/v1/app/{applicationGUID}/card");
    when(worldlineProperties.getAppCardListEndpoint()).thenReturn(
        "/api/v1/app/{applicationGuid}/cards");
    when(worldlineProperties.getAppSubmitEndpoint()).thenReturn("/api/v1/app/submit");
    when(worldlineProperties.getHostedPageAppInitEndpoint())
        .thenReturn("/api/v1/app/{applicationGUID}/hostedPage/init");
    when(worldlineProperties.getBankDetailsStatusEndpoint())
        .thenReturn("/api/v1/app/BankDetailsStatus/{hostedPageGuid}");

    wm = new WireMockServer(options().port(8080));
    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testWorldlineClient_ShouldReturnException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String body = """
        {
            "ResponseCode": "500",
            "Data": null,
            "Errors": [
                {
                    "Code": "500",
                    "Target": null,
                    "Message": "Server Error Occurred."
                }
            ]
        }
        """;

    wm.stubFor(any(urlMatching("^.*app/init.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(new Body(body))));

    // Act
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.appInitWorldline(worldlineAppInitRequestDto, headersDto));
  }

  @Test
  void testWorldlineClient_fetchWorldlineApplicationDetails_ShouldReturnDetails() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String detailsResponseBody = """
        {
            "responseCode": "200",
            "data": {
                "applicationDetails": {
                    "applicationGuid": "appId",
                    "status": "active"
                }
            },
            "errors": []
        }
        """;

    wm.stubFor(any(urlMatching("^.*details.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(200)
            .withResponseBody(new Body(detailsResponseBody))));

    // Act
    var response = worldlineClient.fetchWorldlineApplicationDetails("appId", headersDto);

    // Assert
    assertNotNull(response);
    assertEquals("appId", response.getData().getApplicationDetails().getApplicationGuid());
    assertEquals("active", response.getData().getApplicationDetails().getStatus());
  }

  @Test
  void testWorldlineClient_fetchWorldlineApplicationDetails_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String errorResponseBody = """
        {
            "ResponseCode": "404",
            "Data": null,
            "Errors": [
                {
                    "Code": "404",
                    "Target": null,
                    "Message": "Application not found."
                }
            ]
        }
        """;

    wm.stubFor(any(urlMatching("^.*details.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.fetchWorldlineApplicationDetails("invalidAppId", headersDto));
  }

  @Test
  void testWorldlineClient_getAppLookupWorldline_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    var lookupNames = List.of(LookupName.TITLE);
    String errorResponseBody = """
        {
            "ResponseCode": "400",
            "Data": null,
            "Errors": [
                {
                    "Code": "400",
                    "Target": "Request",
                    "Message": "Invalid lookupName.\\nAllowed values are: Title, TradingStyle, EstimatedMonthlySpend, HotelBrandPolicies, HotelBookingRoles, ISOCountryCodes, TimeTrading, IndustrySector, RegistrationQuestions"
                }
            ]
        }
        """;

    wm.stubFor(any(urlMatching("^.*lookup.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(400)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.getAppLookup(lookupNames, headersDto));
  }

  @Test
  void testWorldlineClient_appCancelWorldline_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    var worldlineAppCancelRequestDto = createWorldlineAppCancelRequestDto();
    String errorResponseBody = """
        {
             "ResponseCode": "404",
             "Data": null,
             "Errors": [
                 {
                     "Code": "404",
                     "Target": null,
                     "Message": "Application not found for the provided ApplicationGUID"
                 }
             ]
        }
        """;

    wm.stubFor(any(urlMatching("^.*cancel.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.appCancelWorldline("invalid-appGuid", headersDto, worldlineAppCancelRequestDto));
  }

  @Test
  void testWorldlineClient_appContactDetailsUpdateWorldline_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    var wlAppContactDetailsUpdateRequestDto = createWLAppContactDetailsUpdateRequestDto();
    String errorResponseBody = """
        {
           "ResponseCode": "404",
           "Data": null,
           "Errors": [
               {
                   "Code": "404",
                   "Target": null,
                   "Message": "Application not found for the provided ApplicationGUID."
               }
           ]
        }
        """;

    wm.stubFor(any(urlMatching("^.*contactDetails.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.appContactDetailsUpdateWorldline(
            "123", wlAppContactDetailsUpdateRequestDto, headersDto));
  }

  @Test
  void testWorldlineClient_appCompanyDetailsUpdate_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    var wlAppCompanyDetailsUpdateRequestDto = createWLAppCompanyDetailsUpdateRequestDto();
    String errorResponseBody = """
        {
           "ResponseCode": "404",
           "Data": null,
           "Errors": [
               {
                   "Code": "404",
                   "Target": null,
                   "Message": "Application not found for the provided ApplicationGUID."
               }
           ]
        }
        """;

    wm.stubFor(any(urlMatching("^.*companyDetails.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.appCompanyDetailsUpdate(
            "123", wlAppCompanyDetailsUpdateRequestDto, headersDto));
  }

  @Test
  void testWorldlineClient_lookupCompanyDetailsWorldline_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String errorResponseBody = """
        {
             "ResponseCode": "400",
             "Data": null,
             "Errors": [
                 {
                     "Code": "400",
                     "Target": "Request",
                     "Message": "Company registration number is invalid"
                 }
             ]
         }
        """;

    wm.stubFor(any(urlMatching("^.*companyDetailsLookup.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(400)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.lookupCompanyDetailsWorldline(
            "123", headersDto));
  }

  @Test
  void testWorldlineClient_deleteApplicationCard_ShouldSucceed() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String applicationGuid = "app-guid";
    String cardGuid = "card-guid";
    String successResponseBody = """
        {
            "responseCode": "200",
            "data": null,
            "errors": []
        }
        """;

    wm.stubFor(delete(urlPathMatching("/api/v1/app/[^/]+/card/[^/]+"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(200)
            .withBody(successResponseBody)));

    // Act
    var response = worldlineClient.deleteApplicationCard(applicationGuid, cardGuid, headersDto);

    // Assert
    assertNotNull(response);
    assertEquals("200", response.getResponseCode());
  }

  @Test
  void testWorldlineClient_deleteApplicationCard_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String applicationGuid = "app-guid";
    String cardGuid = "card-guid";
    String errorResponseBody = """
        {
            "responseCode": "404",
            "data": null,
            "errors": [
                {
                    "code": "404",
                    "target": null,
                    "message": "Card not found for the provided ApplicationGUID."
                }
            ]
        }
        """;

    wm.stubFor(delete(urlPathMatching("/api/v1/app/[^/]+/card/[^/]+"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withBody(errorResponseBody)));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.deleteApplicationCard(applicationGuid, cardGuid, headersDto));
  }

  @Test
  void testWorldlineClient_addApplicationCard_ShouldSucceed() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String applicationGuid = "app-guid";
    String successResponseBody = """
        {
            "responseCode": "200",
            "data": {
              "cardGuid": "6bb273f9-1439-4d23-935b-b8d1535e8776"
            },
            "errors": []
        }
        """;

    wm.stubFor(any(urlPathMatching("^.*card.*$"))
        .willReturn(okJson(successResponseBody)));

    // Act
    var response = worldlineClient.addApplicationCard(applicationGuid, createAddApplicationCardDetails(), headersDto);

    // Assert
    assertNotNull(response);
    assertEquals("200", response.getResponseCode());
  }

  @Test
  void testWorldlineClient_addApplicationCard_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String applicationGuid = "app-guid";
    String errorResponseBody = """
        {
            "responseCode": "422",
            "data": null,
            "errors": [
                {
                    "code": "NotEmptyValidator",
                    "target": "CardName",
                    "message": "CardName cannot be empty"
                }
            ]
        }
        """;

    var addApplicationCardDetails = createAddApplicationCardDetails();

    wm.stubFor(any(urlPathMatching("^.*card.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(422)
            .withBody(errorResponseBody)));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.addApplicationCard(applicationGuid, addApplicationCardDetails, headersDto));
  }


  @Test
  void testWorldlineClient_getAppCards_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String errorResponseBody = """
        {
            "responseCode": "404",
            "data": null,
            "errors": [
                {
                    "code": "404",
                    "target": null,
                    "message": "Application not found for the provided ApplicationGUID"
                }
            ]
        }
        """;

    wm.stubFor(get(urlMatching("^.*cards.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.getAppCards(
            "123", 1, 12, headersDto));
  }


  @Test
  void testWorldlineClient_submitApplication_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    var submitApplicationRequestDetailsDto = createSubmitApplicationRequestDetailsDto();
    String errorResponseBody = """
        {
            "responseCode": "422",
            "data": null,
            "errors": [
                {
                    "code": "NotNullValidator",
                    "target": "RegistrationQuestion",
                    "message": "RegistrationQuestion is required"
                }
            ]
        }
        """;

    wm.stubFor(any(urlPathMatching("^.*submit.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(422)
            .withBody(errorResponseBody)));

    // Act & Assert
    assertThrows( WorldlineResponseException.class,
        () -> worldlineClient.submitApplication(submitApplicationRequestDetailsDto, headersDto));
  }

  @Test
  void testWorldlineClient_hostedPageAppInit_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String errorResponseBody = """
        {
            "responseCode": "404",
            "data": null,
            "errors": [
                {
                    "code": "404",
                    "target": null,
                    "message": "Application not found for the provided ApplicationGUID."
                }
            ]
        }
        """;

    wm.stubFor(any(urlMatching("^.*hostedPage.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.hostedPageAppInit(
            "application-guid", headersDto));
  }

  @Test
  void testWorldlineClient_bankDetailsStatus_ShouldThrowException() {
    WorldlineClient worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
    WorldlineHeadersDto headersDto = createWorldlineHeadersDto();
    String errorResponseBody = """
        {
            "responseCode": "404",
            "data": null,
            "errors": [
                {
                    "code": "404",
                    "target": null,
                    "message": "Record does not exists for the provided HostedPageGuid."
                }
            ]
        }
        """;

    wm.stubFor(any(urlMatching("^.*BankDetailsStatus.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(new Body(errorResponseBody))));

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> worldlineClient.bankDetailsStatus(
            "hostedPageGuid", headersDto));
  }

  private SubmitApplicationRequestDetailsDto createSubmitApplicationRequestDetailsDto() {
    return SubmitApplicationRequestDetailsDto.builder()
        .applicationGuid("app-guid")
        .hostedPageGuid("hosted-page-guid")
        .hotelBookingRole("hotel-booking-role")
        .isDirectDebit(false)
        .termsAndConditionAccepted("Y")
        .registrationQuestion("registration-question")
        .registrationAnswer("registration-answer")
        .build();
  }


  private WorldlineHeadersDto createWorldlineHeadersDto() {
    return WorldlineHeadersDto.builder()
        .ipAddress("1.1.1.1")
        .cultureCode("en-GB")
        .companyNumber(35)
        .trustedPartnerCredentialsDto(TrustedPartnerCredentialsDto.builder()
            .username("username")
            .password("password")
            .build())
        .build();
  }

  private final WorldlineAppInitRequestDto worldlineAppInitRequestDto = WorldlineAppInitRequestDto.builder()
      .email("john.doe@email.com")
      .incentiveCode("123")
      .campaignCode("C123")
      .build();

  private WLAppContactDetailsUpdateRequestDto createWLAppContactDetailsUpdateRequestDto() {
    return WLAppContactDetailsUpdateRequestDto.builder()
        .title("Mr")
        .foreName("John")
        .lastName("Doe")
        .position("Developer")
        .telephone("+123")
        .email("john.doe@email.com")
        .build();
  }

  private WorldlineAppCancelRequestDto createWorldlineAppCancelRequestDto() {
    return WorldlineAppCancelRequestDto.builder()
        .reasonDescription("User triggered cancel.")
        .build();
  }

  private WLAppCompanyDetailsUpdateRequestDto createWLAppCompanyDetailsUpdateRequestDto() {
    return WLAppCompanyDetailsUpdateRequestDto.builder()
        .companyName("Company Name")
        .estMonthlySpend("£4,000")
        .companyType("Partnership")
        .build();
  }

  private AddApplicationCardDetails createAddApplicationCardDetails() {
    return AddApplicationCardDetails.builder()
        .myCard(true)
        .cardLimit(100)
        .cardName("John Doe")
        .build();
  }


}
