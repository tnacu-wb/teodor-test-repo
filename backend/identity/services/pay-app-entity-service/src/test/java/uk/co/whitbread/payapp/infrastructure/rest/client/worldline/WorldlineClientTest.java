package uk.co.whitbread.payapp.infrastructure.rest.client.worldline;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardDetails;
import uk.co.whitbread.payapp.domain.model.in.LookupName;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.exceptions.WorldlineResponseException;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.SubmitApplicationRequestDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.TrustedPartnerCredentialsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppCompanyDetailsUpdateRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppContactDetailsUpdateRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppCancelRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppInitRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppPreCheckRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineHeadersDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppCompanyDetailsLookupDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppDetailsDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppInitDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppLookupDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppPreCheckDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.FetchApplicationDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.GetAppCardsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.GetUserPreferencesResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.SubmitApplicationDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAddApplicationCardDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAddApplicationCardResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppCancelResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppCompanyDetailsLookupResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppCompanyDetailsUpdateResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppContactDetailsUpdateResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppInitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppLookupResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLDeleteCardDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLHostedPageAppInitDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLHostedPageAppInitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WorldlineAppPreCheckResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WorldlineBankDetailsStatusResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;

@ExtendWith(MockitoExtension.class)
class WorldlineClientTest {

  private WorldlineClient worldlineClient;

  @Mock
  private WebClient worldlineWebClient;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private WorldlineProperties worldlineProperties;

  @BeforeEach
  void init() {
    worldlineClient = new WorldlineClient(worldlineWebClient, worldlineProperties);
  }

  @Test
  void appInitWorldline__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppInitResponseDto.class)).thenReturn(
        createWorldlineAppInitResponseDto());

    // Act
    var worldlineAppInitResponseDto = worldlineClient.appInitWorldline(
        createWorldlineAppInitRequestDto(), createWorldlineHeadersDto());

    // Assert
    assertThat(worldlineAppInitResponseDto, notNullValue());
  }

  @Test
  void fetchWorldlineApplicationDetails__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(FetchApplicationDetailsResponseDto.class)).thenReturn(
        createFetchApplicationDetailsResponseDto());

    // Act
    var fetchWorldlineApplicationDetails = worldlineClient.fetchWorldlineApplicationDetails(
        "appGuid", createWorldlineHeadersDto());

    // Assert
    assertNotNull(fetchWorldlineApplicationDetails);
  }

  @Test
  void fetchWorldlineApplicationDetails__shouldFail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(FetchApplicationDetailsResponseDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_APP_FETCH_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.fetchWorldlineApplicationDetails("appGuid", wlHeadersDto));
  }

  @Test
  void fetchWorldlineApplicationDetails__shouldReturnEmptyResponse() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(FetchApplicationDetailsResponseDto.class)).thenReturn(
        Mono.just(FetchApplicationDetailsResponseDto.builder().build()));

    // Act
    var fetchWorldlineApplicationDetails = worldlineClient.fetchWorldlineApplicationDetails(
        "appGuid", createWorldlineHeadersDto());

    // Assert
    assertNotNull(fetchWorldlineApplicationDetails);
  }

  @Test
  void fetchWorldlineApplicationDetails__shouldReturnNull() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(FetchApplicationDetailsResponseDto.class)).thenReturn(
        Mono.just(FetchApplicationDetailsResponseDto.builder()
                .data(AppDetailsDataDto.builder().build())
            .build()));

    // Act
    var fetchWorldlineApplicationDetails = worldlineClient.fetchWorldlineApplicationDetails(
        "appGuid", createWorldlineHeadersDto());

    // Assert
    assertNotNull(fetchWorldlineApplicationDetails);
    assertNull(fetchWorldlineApplicationDetails.getData().getApplicationDetails());
  }

  @Test
  void getUserPreferences__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetUserPreferencesResponseDto.class))
        .thenReturn(Mono.just(GetUserPreferencesResponseDto.builder().build()));

    // Act
    var userPreferences = worldlineClient
        .getUserPreferences("tetheredUserGuid", createWorldlineHeadersDto());

    // Assert
    assertNotNull(userPreferences);
  }

  @Test
  void getUserPreferences__shouldFail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetUserPreferencesResponseDto.class)).thenThrow(
        new WorldlineResponseException(ErrorCode.WORLDLINE_USER_PREFERENCES_FETCH_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.getUserPreferences("tetheredUserGuid", wlHeadersDto));
  }

  @Test
  void appContactDetailsUpdateWorldline__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppContactDetailsUpdateResponseDto.class))
        .thenReturn(createWLAppContactDetailsUpdateResponseDto());

    // Act
    var wlAppContactDetailsUpdateResponseDto = worldlineClient.appContactDetailsUpdateWorldline(
        "123", createWLAppContactDetailsUpdateRequestDto(), createWorldlineHeadersDto());

    // Assert
    assertThat(wlAppContactDetailsUpdateResponseDto, notNullValue());
  }

  @Test
  void appContactDetailsUpdateWorldline__shouldFail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();
    var wlAppContactDetailsUpdateRequestDto = createWLAppContactDetailsUpdateRequestDto();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppContactDetailsUpdateResponseDto.class))
        .thenThrow(new WorldlineResponseException(
            ErrorCode.WORLDLINE_APP_CONTACT_DETAILS_UPDATE_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.appContactDetailsUpdateWorldline(
            "123", wlAppContactDetailsUpdateRequestDto, wlHeadersDto));
  }

  @Test
  void appCompanyDetailsUpdate__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppCompanyDetailsUpdateResponseDto.class))
        .thenReturn(createWLAppCompanyDetailsUpdateResponseDto());

    // Act
    var wlAppCompanyDetailsUpdateResponseDto = worldlineClient.appCompanyDetailsUpdate(
        "123", createWLAppCompanyDetailsUpdateRequestDto(), createWorldlineHeadersDto());

    // Assert
    assertThat(wlAppCompanyDetailsUpdateResponseDto, notNullValue());
  }

  @Test
  void appCompanyDetailsUpdate__shouldFail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();
    var wlAppCompanyDetailsUpdateRequestDto = createWLAppCompanyDetailsUpdateRequestDto();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppCompanyDetailsUpdateResponseDto.class))
        .thenThrow(new WorldlineResponseException(
            ErrorCode.WORLDLINE_APP_COMPANY_DETAILS_UPDATE_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.appCompanyDetailsUpdate(
            "123", wlAppCompanyDetailsUpdateRequestDto, wlHeadersDto));
  }

  @Test
  void getAppLookupWorldline__success() {
    // Arrange
    var lookupNames = List.of(LookupName.TITLE);

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppLookupResponseDto.class)).thenReturn(
        createWLAppLookupResponseDto());

    // Act
    var getAppLookupWorldline = worldlineClient.getAppLookup(lookupNames,
        createWorldlineHeadersDto());

    // Assert
    assertThat(getAppLookupWorldline, notNullValue());
  }

  @Test
  void getAppLookupWorldline_shouldFail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();
    var lookupNames = List.of(LookupName.TITLE);

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppLookupResponseDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_APP_LOOKUP_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.getAppLookup(lookupNames, wlHeadersDto));
  }

  @Test
  void appCancelWorldline__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppCancelResponseDto.class)).thenReturn(
        createWLAppCancelResponseDto());

    // Act
    var response = worldlineClient.appCancelWorldline("appGuid", createWorldlineHeadersDto(),
        createWorldlineAppCancelRequestDto());

    // Assert
    assertThat(response, notNullValue());
  }

  @Test
  void appCancelWorldline__shouldFail() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppCancelResponseDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_APP_CANCEL_ERROR,
            "Error when calling WorldLine REST API"));
    var worldlineHeaders = createWorldlineHeadersDto();
    var worldlineRequest = createWorldlineAppCancelRequestDto();

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.appCancelWorldline("appGuid", worldlineHeaders, worldlineRequest));
  }

  @Test
  void lookupCompanyDetailsWorldline__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppCompanyDetailsLookupResponseDto.class)).
        thenReturn(createWLAppCompanyDetailsLookupResponseDto());

    // Act
    var lookupCompanyDetailsWorldlineResponse = worldlineClient.
        lookupCompanyDetailsWorldline("dummy", createWorldlineHeadersDto());

    // Assert
    assertNotNull(lookupCompanyDetailsWorldlineResponse);
  }

  @Test
  void lookupCompanyDetailsWorldline__shouldFail() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAppCompanyDetailsLookupResponseDto.class)).
        thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_APP_CANCEL_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () -> worldlineClient.
        lookupCompanyDetailsWorldline("dummy", createWorldlineHeadersDto()));

  }

  @Test
  void addApplicationCard__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAddApplicationCardResponseDto.class)).thenReturn(
        createWLAddApplicationCardResponseDto());

    // Act
    var wlAddApplicationCardResponseDto = worldlineClient.addApplicationCard(
        "appGuid", createAddApplicationCardDetails(), createWorldlineHeadersDto());

    //Assert
    assertThat(wlAddApplicationCardResponseDto, notNullValue());
  }

  @Test
  void addApplicationCard__shouldFail() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLAddApplicationCardResponseDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_APP_ADD_CARD_ERROR, "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.addApplicationCard("appGuid", createAddApplicationCardDetails(), createWorldlineHeadersDto()));
  }

  @Test
  void deleteApplicationCard_ShouldSucceed() {
    // Arrange
    var applicationGuid = "app-guid";
    var cardGuid = "card-guid";
    var worldlineHeadersDto = createWorldlineHeadersDto();
    var expectedResponse = WLDeleteCardDto.builder().responseCode("200").build();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLDeleteCardDto.class)).thenReturn(Mono.just(expectedResponse));

    // Act
    var response = worldlineClient.deleteApplicationCard(applicationGuid, cardGuid,
        worldlineHeadersDto);

    // Assert
    assertThat(response, notNullValue());
    assertEquals("200", response.getResponseCode());
  }

  @Test
  void deleteApplicationCard_ShouldThrowExceptionOnError() {
    // Arrange
    var applicationGuid = "app-guid";
    var cardGuid = "card-guid";
    var worldlineHeadersDto = createWorldlineHeadersDto();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLDeleteCardDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_APP_CARD_DELETE_ERROR,
            "Error deleting card"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.deleteApplicationCard(applicationGuid, cardGuid, worldlineHeadersDto));
  }

  @Test
  void getAppCards__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetAppCardsResponseDto.class))
        .thenReturn(Mono.just(GetAppCardsResponseDto.builder().build()));

    // Act
    var getAppCardsResponse = worldlineClient.getAppCards("appGuid", 1, 12,
        createWorldlineHeadersDto());

    // Assert
    assertThat(getAppCardsResponse, notNullValue());
  }

  @Test
  void getAppCards__shouldFail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetAppCardsResponseDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_APP_CARDS_GET_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.getAppCards("appGuid", 1, 12, wlHeadersDto));
  }

  @Test
  void submitApplication__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(SubmitApplicationDto.class)).thenReturn(
        creteSubmitApplicationDto());

    // Act
    var submitApplicationDto = worldlineClient.submitApplication(
        createSubmitApplicationRequestDetailsDto(), createWorldlineHeadersDto());

    //Assert
    assertThat(submitApplicationDto, notNullValue());
  }

  @Test
  void submitApplication__fail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();
    var submitApplicationRequestDetailsDto = createSubmitApplicationRequestDetailsDto();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(SubmitApplicationDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_APP_SUBMIT_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.submitApplication(submitApplicationRequestDetailsDto, wlHeadersDto));
  }

  @Test
  void appPreCheck__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WorldlineAppPreCheckResponseDto.class)).thenReturn(
        createWorldlineAppPreCheckResponseDto());

    // Act
    var response = worldlineClient.appPreCheck(
        createWorldlineAppPreCheckRequestDto(), createWorldlineHeadersDto());

    //Assert
    assertThat(response, notNullValue());
    assertEquals("200", response.responseCode());
  }

  @Test
  void hostedPageAppInit__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLHostedPageAppInitResponseDto.class)).thenReturn(
        createWLHostedPageAppInitResponseDto());

    // Act
    var response = worldlineClient.hostedPageAppInit(
        "application-guid", createWorldlineHeadersDto());

    //Assert
    assertThat(response, notNullValue());
    assertEquals("200", response.getResponseCode());
  }

  @Test
  void hostedPageAppInit__ShouldFail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WLHostedPageAppInitResponseDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_HOSTED_PAGE_APP_INIT_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.hostedPageAppInit("application-guid", wlHeadersDto));
  }

  @Test
  void bankDetailsStatus__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WorldlineBankDetailsStatusResponseDto.class))
        .thenReturn(Mono.just(WorldlineBankDetailsStatusResponseDto.builder().build()));

    // Act
    var bankDetailsStatusResponse = worldlineClient.bankDetailsStatus("hostedPageGuid",
        createWorldlineHeadersDto());

    // Assert
    assertThat(bankDetailsStatusResponse, notNullValue());
  }

  @Test
  void bankDetailsStatus__shouldFail() {
    // Arrange
    var wlHeadersDto = createWorldlineHeadersDto();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(worldlineWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(WorldlineBankDetailsStatusResponseDto.class))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_BANK_DETAILS_STATUS_ERROR,
            "Error when calling WorldLine REST API"));

    // Act & Assert
    assertThrows(WorldlineResponseException.class, () ->
        worldlineClient.bankDetailsStatus("hostedPageGuid", wlHeadersDto));
  }

  private WorldlineAppPreCheckRequestDto createWorldlineAppPreCheckRequestDto() {
    return new WorldlineAppPreCheckRequestDto("john.doe@email.com");
  }

  Mono<WorldlineAppPreCheckResponseDto> createWorldlineAppPreCheckResponseDto() {
    return Mono.just(WorldlineAppPreCheckResponseDto.builder()
        .responseCode("200")
        .data(new AppPreCheckDataDto(true, "123"))
        .errors(null)
        .build());
  }

  Mono<SubmitApplicationDto> creteSubmitApplicationDto() {
    return Mono.just(SubmitApplicationDto.builder()
        .responseCode("200")
        .data("Success")
        .errors(null)
        .build());
  }

  SubmitApplicationRequestDetailsDto createSubmitApplicationRequestDetailsDto() {
    return SubmitApplicationRequestDetailsDto.builder()
        .applicationGuid("123")
        .registrationQuestion("registrationQuestion")
        .registrationAnswer("registrationAnswer")
        .termsAndConditionAccepted("Y")
        .isDirectDebit(false)
        .hotelBookingRole("hotelBookingRole")
        .hostedPageGuid("hostedPageGuid")
        .build();
  }

  Mono<WLAppCompanyDetailsLookupResponseDto> createWLAppCompanyDetailsLookupResponseDto() {
    return Mono.just(WLAppCompanyDetailsLookupResponseDto.builder()
        .responseCode("200")
        .data(List.of(AppCompanyDetailsLookupDto.builder()
            .creditAgencyReference("123")
            .companyName("dummy")
            .build()))
        .errors(null)
        .build());
  }

  Mono<WLAppCancelResponseDto> createWLAppCancelResponseDto() {
    WLAppCancelResponseDto responseDto = WLAppCancelResponseDto.builder()
        .responseCode("200")
        .build();

    return Mono.just(responseDto);
  }

  WorldlineAppCancelRequestDto createWorldlineAppCancelRequestDto() {
    return WorldlineAppCancelRequestDto.builder()
        .reasonDescription("User triggered cancel.")
        .build();
  }

  Mono<FetchApplicationDetailsResponseDto> createFetchApplicationDetailsResponseDto() {
    FetchApplicationDetailsResponseDto responseDto = FetchApplicationDetailsResponseDto.builder()
        .responseCode("200")
        .data(AppDetailsDataDto.builder()
            .applicationDetails(AppDetailsDto.builder()
                .status("Outstanding")
                .build())
            .build())
        .errors(null)
        .build();

    return Mono.just(responseDto);
  }

  WorldlineAppInitRequestDto createWorldlineAppInitRequestDto() {
    return WorldlineAppInitRequestDto.builder()
        .email("john.doe@email.com")
        .campaignCode("C123")
        .incentiveCode("123")
        .build();
  }

  WorldlineHeadersDto createWorldlineHeadersDto() {
    return WorldlineHeadersDto.builder()
        .companyNumber(35)
        .trustedPartnerCredentialsDto(TrustedPartnerCredentialsDto.builder()
            .username("test")
            .password("pass")
            .build())
        .cultureCode("en-GB")
        .ipAddress("1.1.1.1")
        .build();
  }

  Mono<WLAppInitResponseDto> createWorldlineAppInitResponseDto() {
    WLAppInitResponseDto responseDto = WLAppInitResponseDto.builder()
        .responseCode("200")
        .data(AppInitDataDto.builder()
            .applicationGUID("123")
            .applicationNumber("123")
            .build())
        .errors(null)
        .build();

    return Mono.just(responseDto);
  }

  Mono<WLAppContactDetailsUpdateResponseDto> createWLAppContactDetailsUpdateResponseDto() {
    WLAppContactDetailsUpdateResponseDto responseDto = WLAppContactDetailsUpdateResponseDto.builder()
        .responseCode("200")
        .data("Success")
        .errors(null)
        .build();

    return Mono.just(responseDto);
  }

  WLAppContactDetailsUpdateRequestDto createWLAppContactDetailsUpdateRequestDto() {
    return WLAppContactDetailsUpdateRequestDto.builder()
        .title("Mr")
        .foreName("John")
        .lastName("Doe")
        .position("Developer")
        .telephone("+123")
        .email("john.doe@email.com")
        .build();
  }

  Mono<WLAppLookupResponseDto> createWLAppLookupResponseDto() {
    var wlAppLookupResponseDto = WLAppLookupResponseDto.builder()
        .responseCode("200")
        .data(AppLookupDataDto.builder()
            .lookupData(Map.of("title", List.of("Mr", "Mrs", "Miss", "Ms", "Doctor")))
            .build())
        .errors(null)
        .build();

    return Mono.just(wlAppLookupResponseDto);
  }

  Mono<WLAppCompanyDetailsUpdateResponseDto> createWLAppCompanyDetailsUpdateResponseDto() {
    WLAppCompanyDetailsUpdateResponseDto responseDto = WLAppCompanyDetailsUpdateResponseDto.builder()
        .responseCode("200")
        .data("Success")
        .errors(null)
        .build();

    return Mono.just(responseDto);
  }

  WLAppCompanyDetailsUpdateRequestDto createWLAppCompanyDetailsUpdateRequestDto() {
    return WLAppCompanyDetailsUpdateRequestDto.builder()
        .companyName("Company Name")
        .estMonthlySpend("£4,000")
        .companyType("Partnership")
        .build();
  }

  AddApplicationCardDetails createAddApplicationCardDetails() {
    return AddApplicationCardDetails.builder()
        .myCard(true)
        .cardLimit(100)
        .cardName("John Doe")
        .build();
  }

  Mono<WLAddApplicationCardResponseDto> createWLAddApplicationCardResponseDto() {
    return Mono.just(WLAddApplicationCardResponseDto.builder()
        .data(WLAddApplicationCardDto.builder()
            .cardGuid("123")
            .build())
        .responseCode("200")
        .build());
  }

  Mono<WLHostedPageAppInitResponseDto> createWLHostedPageAppInitResponseDto() {
    return Mono.just(WLHostedPageAppInitResponseDto.builder()
        .responseCode("200")
        .data(WLHostedPageAppInitDto.builder()
            .hostedPageGuid("hosted-page-guid")
            .build())
        .errors(null)
        .build());
  }

}
