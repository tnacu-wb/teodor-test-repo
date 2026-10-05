package uk.co.whitbread.booking.infrastructure.rest.client.content.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.in.HotelInformationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.in.MealsRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.HotelInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.MealsInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.RateInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.UpsellItemsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.properties.ContentProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions.InternalBasketException;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationRateInformationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRateInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ContentClientTest {

  private static final String COUNTRY = "country";
  private static final String LANGUAGE = "language";
  private static final String HOTEL_ID = "hotelId";
  private static final String BOOKING_CHANNEL = "channel";
  private static final String SUB_CHANNEL = "subchannel";

  @InjectMocks
  private ContentClient contentClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private ContentProperties contentProperties;

  @Test
  void getHotelRateInformation__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RateInformationResponseDto.class)).thenReturn(Mono.just(new RateInformationResponseDto()));

    // Act
    var response = contentClient.getHotelRateInformation("LONEUS", "gb", "en");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getHotelRateInformation__throws40xException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(RateInformationResponseDto.class))
        .thenReturn(Mono.error(
            new ContentException("exp", "message", new Exception(), 100)));
    // Act & Assert
    assertThrows(ContentException.class,
        () -> contentClient.getHotelRateInformation("LONEUS", "gb", "en"));
  }

  @Test
  void getHotelRateInformation__throws50xException() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(RateInformationResponseDto.class))
        .thenReturn(Mono.error(
            new ContentException("exp", "message", new Exception(), 100)));
    // Act & Assert
    assertThrows(ContentException.class,
        () -> contentClient.getHotelRateInformation("LONEUS", "gb", "en"));
  }

  @Test
  void getReservationRateInformation_success() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationRateInformationResponseDto.class)).thenReturn(
            Mono.just(new ReservationRateInformationResponseDto()));

    // Act
    var response = contentClient.getRateInformation(mockReservationRateInfoRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getReservationRateInformation_throwsBasketException() {
    var mockReservationRateInfoRequest = ReservationRateInformationRequestDto.builder().channel(BOOKING_CHANNEL).build();
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(ReservationRateInformationResponseDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));
    // Assert
    assertThrows(InternalBasketException.class,
            () -> contentClient.getRateInformation(mockReservationRateInfoRequest));
  }

  private ReservationRateInformationRequestDto mockReservationRateInfoRequest() {
    return ReservationRateInformationRequestDto
            .builder()
            .country(COUNTRY)
            .language(LANGUAGE)
            .hotelId(HOTEL_ID)
            .channel(BOOKING_CHANNEL)
            .build();
  }

  @Test
  void getMealsInfo__ShouldReturnOK() {

    //Arrange
    MealsRequestDto mealsRequestDto = createMealsInfoRequest();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    initWebClient();
    when(responseSpec.bodyToMono(MealsInfoResponseDto.class)).thenReturn(
        mockMealsInfoResponse());

    //Act
    final var mealsAEMResponse =
        contentClient.getMealsInformation(mealsRequestDto);

    //Assert
    assertThat(mealsAEMResponse, notNullValue());
    var mealsResponse = mealsAEMResponse.getUpsellItems().get(0);
    assertThat(mealsResponse.getCode(), is("BFADCT"));
    assertThat(mealsResponse.getName(), is("Continental breakfast"));
    assertThat(mealsResponse.getDescription(), is("description"));
    assertThat(mealsResponse.getImages().get(0), is("/content/dam/images.png"));
    assertThat(mealsResponse.getShow(), is(true));
    assertThat(mealsResponse.getFreeBreakfastOption(), is(false));
    assertThat(mealsResponse.getFreeBreakfastTrigger(), is(false));
  }

  @Test
  void getMealsInfo_ShouldReturnException() {
    MealsRequestDto mealsRequestDto = createMealsInfoRequest();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(MealsInfoResponseDto.class))
        .thenReturn(Mono.error(
            new InternalBasketException("exp", "message", new Exception(), 100)));
    // Assert
    assertThrows(InternalBasketException.class,
        () -> contentClient.getMealsInformation(mealsRequestDto));
  }

  @Test
  void getMealsInformation__throws50xException() {
    // Arrange
    MealsRequestDto mealsRequestDto = createMealsInfoRequest();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(MealsInfoResponseDto.class))
        .thenReturn(Mono.error(
            new ContentException("exp", "message", new Exception(), 100)));
    // Act & Assert
    assertThrows(ContentException.class,
        () -> contentClient.getMealsInformation(mealsRequestDto));
  }

  @Test
  void getHotelInformation_WhenCalled_ThenReturnOK() {
    var hotelInformationRequestDto = HotelInformationRequestDto.builder()
          .hotelId(HOTEL_ID)
          .channel(BOOKING_CHANNEL)
          .subchannel(SUB_CHANNEL)
          .country(COUNTRY)
          .language(LANGUAGE)
          .build();
    var expectedResponse = HotelInformationResponseDto.builder()
          .brand("brand")
          .build();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    initWebClient();
    when(responseSpec.bodyToMono(HotelInformationResponseDto.class)).thenReturn(Mono.just(expectedResponse));

    var hotelInformation = contentClient.getHotelInformation(hotelInformationRequestDto);

    assertThat(hotelInformation, notNullValue());
    assertEquals(expectedResponse, hotelInformation);
  }

  @Test
  void getHotelInformation_WhenDeserializationFails_ThenThrowContentException() {
    var hotelInformationRequestDto = HotelInformationRequestDto.builder()
          .hotelId(HOTEL_ID)
          .channel(BOOKING_CHANNEL)
          .subchannel(SUB_CHANNEL)
          .country(COUNTRY)
          .language(LANGUAGE)
          .build();
    var expectedException = new ContentException("exp", "message", new Exception(), 100);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelInformationResponseDto.class))
          .thenReturn(Mono.error(expectedException));

    assertThrows(ContentException.class,
          () -> contentClient.getHotelInformation(hotelInformationRequestDto));
  }

  @Test
  void getHotelInformation_WhenServerRespondsWithError_ThenThrowContentException() {
    var hotelInformationRequestDto = HotelInformationRequestDto.builder()
          .hotelId(HOTEL_ID)
          .channel(BOOKING_CHANNEL)
          .subchannel(SUB_CHANNEL)
          .country(COUNTRY)
          .language(LANGUAGE)
          .build();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    var responseSpecMock = mock(WebClient.ResponseSpec.class);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
          .thenAnswer(p -> {
            var statusPredicate = p.getArgument(0, Predicate.class);
            Function<ClientResponse, Mono<? extends Throwable>> errorFunction = p.getArgument(1, Function.class);
            var clientResponse = ClientResponse
                  .create(HttpStatus.INTERNAL_SERVER_ERROR, ExchangeStrategies.builder().codecs(configurer -> {
                    configurer.defaultCodecs().jacksonJsonDecoder(
                        new JacksonJsonDecoder(JsonMapper.builder()
                            .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                            .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                            .findAndAddModules()
                            .build()));
                  }).build())
                  .header("Content-Type", "application/json")
                  .body("{ \"debugMessage\": \"wrong\", \"errorCode\": 222, \"globalErrTextTemplate\": \"internal.server.exception\" }")
                  .build();
            if (statusPredicate.test(clientResponse.statusCode())) {
              errorFunction.apply(clientResponse).block();
            }
            return responseSpecMock;
          });

    var contentException = assertThrows(ContentException.class,
          () -> contentClient.getHotelInformation(hotelInformationRequestDto));

    assertEquals(222, contentException.getErrorCode());
    assertEquals("wrong", contentException.getDebugMessage());
    assertEquals("internal.server.exception", contentException.getGlobalErrTextTemplate());
  }

  @Test
  void getHotelInformation_WhenConstructingRequest_ThenProperParametersPassed() {
    var hotelInformationRequestDto = HotelInformationRequestDto.builder()
          .hotelId(HOTEL_ID)
          .channel(BOOKING_CHANNEL)
          .subchannel(SUB_CHANNEL)
          .country(COUNTRY)
          .language(LANGUAGE)
          .build();
    var expectedResponse = HotelInformationResponseDto.builder()
          .brand("brand")
          .build();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(contentProperties.getHotelInfoEndpoint()).thenReturn("/some/url");
    var uriBuilderMock = mock(UriBuilder.class);
    when(requestHeadersUriSpec.uri(any(Function.class)))
          .thenAnswer(p -> {
            Function<UriBuilder, URI> function = p.getArgument(0, Function.class);
            when(uriBuilderMock.path(any())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(any(String.class), any(String.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(any(String.class))).thenReturn(new URI("/some/url"));
            function.apply(uriBuilderMock);

            return requestHeadersUriSpec;
          });
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInformationResponseDto.class)).thenReturn(Mono.just(expectedResponse));

    var hotelInformation = contentClient.getHotelInformation(hotelInformationRequestDto);

    assertEquals(expectedResponse, hotelInformation);
    verify(uriBuilderMock).path("/some/url");
    verify(uriBuilderMock).queryParam("channel", hotelInformationRequestDto.getChannel());
    verify(uriBuilderMock).queryParam("subchannel", hotelInformationRequestDto.getSubchannel());
    verify(uriBuilderMock).queryParam("language", hotelInformationRequestDto.getLanguage());
    verify(uriBuilderMock).queryParam("country", hotelInformationRequestDto.getCountry());
  }

  private void initWebClient() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }

  private Mono<MealsInfoResponseDto> mockMealsInfoResponse() {
    var upsellItems = new UpsellItemsDto().builder()
        .code("BFADCT")
        .name("Continental breakfast")
        .description("description")
        .images(Collections.singletonList("/content/dam/images.png"))
        .attachments(Collections.emptyList())
        .show(true)
        .freeBreakfastOption(false)
        .freeBreakfastCode("BFAD")
        .freeBreakfastTrigger(false)
        .build();

    var mealsResponseAemDto = MealsInfoResponseDto.builder()
        .upsellItems(Collections.singletonList(upsellItems))
        .build();

    return Mono.just(mealsResponseAemDto);
  }

  private MealsRequestDto createMealsInfoRequest() {
    return MealsRequestDto.builder()
        .country("gb")
        .language("en")
        .hotelId("GLASTA")
        .build();
  }
}
