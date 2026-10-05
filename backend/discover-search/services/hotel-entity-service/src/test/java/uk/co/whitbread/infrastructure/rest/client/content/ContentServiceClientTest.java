package uk.co.whitbread.infrastructure.rest.client.content;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.Matchers.is;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.domain.exceptions.HotelAvailabilityBadReqException;
import uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto;
import uk.co.whitbread.hotel.content.generated.models.GlobalConfigDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.content.generated.models.MealsInfoResponseDto;
import uk.co.whitbread.hotel.content.generated.models.SearchRulesDto;
import uk.co.whitbread.infrastructure.config.ContentServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.cache.exceptions.ContentServiceException;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;
import wiremock.org.hamcrest.MatcherAssert;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ContentServiceClientTest {

  private static final String EN = "en";
  private static final String GB = "gb";

  private static final String HOTEL_ID = "hotelId";
  public static final String CHANNEL_PI = "PI";
  public static final String BRAND_HUB = "HUB";


  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private ContentServiceProperties contentServiceProperties;
  @InjectMocks
  private ContentServiceClient contentServiceClient;

  @Test
  void triggerHotelFacilitiesFilterUpdate__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    assertDoesNotThrow(() -> this.contentServiceClient.triggerHotelFacilitiesFilterUpdate());

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void triggerHotelFacilitiesFilterUpdate__ShouldReturnException() {

    //Arrange
    String error = "Error while trying to get max rooms rule response";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.toBodilessEntity()).thenReturn(
            Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
            () -> contentServiceClient.triggerHotelFacilitiesFilterUpdate());

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void triggerHotelsOpeningSoonCacheUpdate__ShouldReturnException() {

    //Arrange
    String error = "Error while trying to get max rooms rule response";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.toBodilessEntity()).thenReturn(
            Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
            () -> contentServiceClient.triggerHotelsOpeningSoonCacheUpdate());

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getHotelBrand__ShouldReturnException() {

    //Arrange
    String error = "Error while trying to get max rooms rule response";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelInformationDto.class)).thenReturn(
            Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
            () -> contentServiceClient.getHotelBrand("hotelId"));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void triggerHotelsOpeningSoonCacheUpdate__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    assertDoesNotThrow(() -> this.contentServiceClient.triggerHotelsOpeningSoonCacheUpdate());

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelBrand__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInformationDto.class)).thenReturn(mockHotelInformation());

    //Act
    var hotelBrand = contentServiceClient.getHotelBrand("HOTEL_ID");

    //Assert
    assertThat(hotelBrand, notNullValue());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getExtrasLabels__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ExtrasLabelDto.class)).thenReturn(mockExtrasLabelDto());

    //Act
    var extrasLabels = contentServiceClient.getExtrasLabels(GB, EN);

    //Assert
    assertThat(extrasLabels, notNullValue());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void  getExtrasLabels__ShouldReturnException() {

    //Arrange
    String error = "Error while trying to get extrasLabels";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(ExtrasLabelDto.class)).thenReturn(
            Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
            () -> contentServiceClient.getExtrasLabels(GB, EN));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getUpsellItems__AndSoftBundles__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MealsInfoResponseDto.class)).thenReturn(mockUpsellItemsDto());

    //Act
    var upsellItems = contentServiceClient.getUpsellItemsAndSoftBundles(HOTEL_ID, GB, EN);

    //Assert
    assertThat(upsellItems, notNullValue());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getUpsellItems__AndSoftBundles__ShouldReturnException() {

    //Arrange
    String error = "Error while trying to get extrasLabels";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(MealsInfoResponseDto.class)).thenReturn(
            Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
            () -> contentServiceClient.getUpsellItemsAndSoftBundles(HOTEL_ID, GB, EN));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getSearchRules__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(SearchRulesDto.class)).thenReturn(mockSearchRulesDto());

    //Act
    var searchRulesResponse = contentServiceClient.getSearchRules(CHANNEL_PI, Optional.of(BRAND_HUB));

    //Assert
    assertThat(searchRulesResponse, notNullValue());
    MatcherAssert.assertThat(searchRulesResponse.getMaxRooms(), is(4));
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void  getSearchRules__ShouldReturnException() {

    //Arrange
    String error = String.format("Error while trying to get searchRules for channel=%s, brand=%s", CHANNEL_PI,
        BRAND_HUB);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(SearchRulesDto.class)).thenReturn(
        Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
        () -> contentServiceClient.getSearchRules(CHANNEL_PI, Optional.of(BRAND_HUB)));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getPreferenceLabels__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {}))
        .thenReturn(Mono.just(Map.of("label1", "label1", "label2", "label2")));

    //Act
    var response = contentServiceClient
        .getPreferencesLabels("gb","en","main","label");

    //Assert
    assertThat(response, notNullValue());
    assertEquals("label1", response.get("label1"));
    assertEquals("label2", response.get("label2"));
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getPreferenceLabels__ShouldReturnException() {
    //Arrange
    String error = "Error while trying to get preference labels";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {}))
        .thenReturn(
            Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
        () -> contentServiceClient.getPreferencesLabels("gb","en","main","label"));

    //Assert
    verifyNoMoreInteractions(webClient);
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getGlobalConfig__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<UriBuilder, URI> function = invocation.getArgument(0);
      var uriBuilder = new DefaultUriBuilderFactory().builder();
      var buildUri = function.apply(uriBuilder);
      assertEquals(String.format("/v1/content/global-config?country=%s&language=%s&brand=%s&channelId=%s",
          GB, EN, "pi", "PI"), buildUri.toString());
      return requestHeadersSpec;
    });
    when(contentServiceProperties.getGlobalConfigEndpoint())
        .thenReturn("//v1/content/global-config");


    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(Mono.just(new GlobalConfigDto()));

    //Act
    var globalConfig = contentServiceClient.getGlobalConfig("gb","en");

    //Assert
    assertThat(globalConfig, notNullValue());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getGlobalConfig__ShouldReturnException() {
    // Arrange
    String error = "Error while trying to get global config";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(GlobalConfigDto.class))
        .thenReturn(Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    // Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
        () -> contentServiceClient.getGlobalConfig("gb", "en"));

    // Assert
    verifyNoMoreInteractions(webClient);
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getHotelInformation__ShouldReturnOK() {
    // Arrange
    String hotelId = "HOTEL_ID";
    var mockHotelInfo = new HotelInformationExtendedDto();
    mockHotelInfo.setHotelId(hotelId);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<UriBuilder, URI> function = invocation.getArgument(0);
      var uriBuilder = new DefaultUriBuilderFactory().builder();
      var buildUri = function.apply(uriBuilder);
      assertEquals(String.format("/v1/content/hotels/HOTEL_ID/information?country=%s&language=%s&channel=%s"
              + "&subchannel=%s", GB, EN, "PI", "WEB"), buildUri.toString());
      return requestHeadersSpec;
    });
    when(contentServiceProperties.getHotelInformationEndpoint())
        .thenReturn("/v1/content/hotels/{hotelId}/information");

    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInformationExtendedDto.class)).thenReturn(Mono.just(mockHotelInfo));

    // Act
    var hotelInfo = contentServiceClient.getHotelInformation(GB, EN, hotelId);

    // Assert
    assertThat(hotelInfo, notNullValue());
    assertEquals(hotelId, hotelInfo.getHotelId());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelInformation__ShouldReturnException() {
    // Arrange
    String hotelId = "HOTEL_ID";
    String error = "Error while trying to get hotel information for hotel: " + hotelId;

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelInformationExtendedDto.class)).thenReturn(
        Mono.error(new ContentServiceException("message", error, new Exception(), 1)));

    // Act
    Exception exception = Assertions.assertThrows(ContentServiceException.class,
        () -> contentServiceClient.getHotelInformation(null, null, hotelId));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
    verifyNoMoreInteractions(webClient);
  }

  private Mono<HotelInformationDto> mockHotelInformation() {
    var hotelInfo = new HotelInformationDto();
    hotelInfo.setBrand("PI");
    return Mono.just(hotelInfo);
  }

  private Mono<ExtrasLabelDto> mockExtrasLabelDto() {
    var extrasLabelDto = new ExtrasLabelDto();
    extrasLabelDto.setExtrasLabels(List.of());
    return Mono.just(extrasLabelDto);
  }

  private Mono<MealsInfoResponseDto> mockUpsellItemsDto() {
    var upsellItemsDto = new MealsInfoResponseDto();
    upsellItemsDto.setUpsellItems(List.of());
    return Mono.just(upsellItemsDto);
  }

  private Mono<SearchRulesDto> mockSearchRulesDto() {
    var searchRulesDto = new SearchRulesDto();
    searchRulesDto.setMaxRooms(4);
    searchRulesDto.setMaxNights(9);
    searchRulesDto.setMaxArrivalDate(364);
    searchRulesDto.setRoomOccupancies(List.of());
    return Mono.just(searchRulesDto);
  }

  @Test
  void getHotelBrand__shouldThrowBadRequestException_when4xxError() {
    // Arrange
    String badRequestMessage = "Hotel not found";
    String debugMessage = "The hotel code provided does not exist in content service";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelInformationDto.class))
        .thenReturn(Mono.error(new HotelAvailabilityBadReqException(
            badRequestMessage, debugMessage, null, 404)));

    // Act
    var thrownException = Assertions.assertThrows(
        HotelAvailabilityBadReqException.class,
        () -> contentServiceClient.getHotelBrand("INVALID"));

    // Assert
    Assertions.assertEquals(debugMessage, thrownException.getDebugMessage());
    Assertions.assertEquals(404, thrownException.getErrorCode());
  }

}
