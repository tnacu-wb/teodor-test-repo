package uk.co.whitbread.content.infrastructure.rest.client.booking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BOOKING_INFORMATION_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_RATE_INFORMATION_EXCEPTION;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.booking.aem.adapter.BookingAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemBookingInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemRateInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.InfoDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.ItemDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.PrivacyPolicyDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.RateClassificationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.BookingInformationRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.out.RateInformationRequestAemDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class BookingAemClientTests {

  @InjectMocks
  private BookingAemClient aemClient;

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

  private String message = "message";

  @Test
  void getBookingInfo__ShouldReturnOK() {

    initWebClient();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AemBookingInformationDto.class)).thenReturn(
        mockAemBookingInformationDtoResponse());

    //Act
    final var bookingAEMResponse =
        aemClient.getBookingInformation(createbookingInformationRequest());

    //Assert
    assertThat(bookingAEMResponse, notNullValue());
    assertThat(bookingAEMResponse.getRestaurantClosedTitle(), is("Restaurant unavailable"));
    assertThat(bookingAEMResponse.getRestaurantClosedMessage(),
        is("We're sorry, the restaurant at this hotel is closed on your selected dates."));
    assertThat(bookingAEMResponse.getMealsNotAvailableTitle(),
        is("Important restaurant information"));
    assertThat(bookingAEMResponse.getMealsNotAvailableMessage(),
        is("We're sorry, the restaurant at this hotel isn't serving breakfasts or Meal Deals on the dates you've selected."));

    assertThat(bookingAEMResponse.getPrivacyPolicy(), notNullValue());
    assertEquals(
        "We need to collect and keep some mandatory information in order to process your booking.",
        bookingAEMResponse.getPrivacyPolicy().getDescription());
    assertEquals("We keep your personal data safe and secure.",
        bookingAEMResponse.getPrivacyPolicy().getTitle());
    assertEquals("View our Privacy Notice", bookingAEMResponse.getPrivacyPolicy().getLinkLabel());
    assertEquals("Find Out More", bookingAEMResponse.getPrivacyPolicy().getMoreInfoLabel());
    assertEquals("/content/dam/global/booking/verisign.png",
        bookingAEMResponse.getPrivacyPolicy().getMoreInfo().get(0).getImage());
    assertEquals(
        "test description",
        bookingAEMResponse.getPrivacyPolicy().getMoreInfo().get(0).getDescription());
    assertEquals("BARTID", bookingAEMResponse.getUpsellitemsConfiguration().get(0).getBartCode());
    assertEquals("CODE", bookingAEMResponse.getUpsellitemsConfiguration().get(0).getCode());
    assertEquals("3", bookingAEMResponse.getUpsellitemsConfiguration().get(0).getOrder());
  }

  @Test
  void getRateInformationForBrand__ShouldReturnOK() {

    initWebClient();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AemRateInformationDto.class)).thenReturn(
        mockAemRateInformationDtoResponse());

    //Act
    final var rateInfoAEMResponse =
        aemClient.getRateInformationForBrand(createRateInformationRequest());

    //Assert
    assertThat(rateInfoAEMResponse, notNullValue());
    assertThat(rateInfoAEMResponse.getRateClassifications(), notNullValue());
    assertEquals("FLEXRATE",
        rateInfoAEMResponse.getRateClassifications().get(0).getRateClassification());
    assertEquals("Flex", rateInfoAEMResponse.getRateClassifications().get(0).getRateName());
    assertEquals("1", rateInfoAEMResponse.getRateClassifications().get(0).getRateOrder());
    assertEquals("", rateInfoAEMResponse.getRateClassifications().get(0).getRateLongDescription());
    assertEquals("Pay now or on arrival, fully refundable with free cancellation up to 1pm",
        rateInfoAEMResponse.getRateClassifications().get(0).getRateDescription());
  }

  @Test
  void getRateInformationForHotel__ShouldReturnOK() {

    initWebClient();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AemRateInformationDto.class)).thenReturn(
        mockAemRateInformationDtoResponse());

    //Act
    final var rateInfoAEMResponse =
        aemClient.getRateInformationForHotel(new RateInformationRequestAemDto());

    //Assert
    assertThat(rateInfoAEMResponse, notNullValue());
    assertThat(rateInfoAEMResponse.getRateClassifications(), notNullValue());
    assertEquals("FLEXRATE",
        rateInfoAEMResponse.getRateClassifications().get(0).getRateClassification());
    assertEquals("Flex", rateInfoAEMResponse.getRateClassifications().get(0).getRateName());
    assertEquals("1", rateInfoAEMResponse.getRateClassifications().get(0).getRateOrder());
    assertEquals("", rateInfoAEMResponse.getRateClassifications().get(0).getRateLongDescription());
    assertEquals("Pay now or on arrival, fully refundable with free cancellation up to 1pm",
        rateInfoAEMResponse.getRateClassifications().get(0).getRateDescription());
  }


  @Test
  void getRateInformationForBrand_ShouldReturnException() {
    initWebClient();
    var exception = new AemResponseException(
        AEM_RATE_INFORMATION_EXCEPTION,
        message,
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> aemClient.getRateInformationForBrand(createRateInformationRequest()));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_RATE_INFORMATION_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(message));
    assertThat(actual.getErrorCode(), is(AEM_RATE_INFORMATION_EXCEPTION.getCode()));
  }

  @Test
  void getBookingInfo_ShouldReturnException() {
    initWebClient();
    var exception = new AemResponseException(
        AEM_BOOKING_INFORMATION_EXCEPTION,
        message,
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> aemClient.getBookingInformation(createbookingInformationRequest()));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_BOOKING_INFORMATION_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(message));
    assertThat(actual.getErrorCode(), is(AEM_BOOKING_INFORMATION_EXCEPTION.getCode()));
  }

  @Test
  void getRateInformationForHotel_ShouldReturnException() {
    initWebClient();
    var exception = new AemResponseException(
        AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION,
        message,
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> aemClient.getRateInformationForBrand(createRateInformationRequest()));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(message));
    assertThat(actual.getErrorCode(), is(AEM_BOOKING_INFORMATION_HOTEL_EXCEPTION.getCode()));
  }

  private Mono<AemBookingInformationDto> mockAemBookingInformationDtoResponse() {
    AemBookingInformationDto bookingInformationDto = new AemBookingInformationDto();
    bookingInformationDto.setRestaurantClosedTitle("Restaurant unavailable");
    bookingInformationDto.setRestaurantClosedMessage(
        "We're sorry, the restaurant at this hotel is closed on your selected dates.");
    bookingInformationDto.setMealsNotAvailableTitle("Important restaurant information");
    bookingInformationDto.setMealsNotAvailableMessage(
        "We're sorry, the restaurant at this hotel isn't serving breakfasts or Meal Deals on the dates you've selected.");
    bookingInformationDto.setPrivacyPolicy(mockPrivacyPolicyDto());
    bookingInformationDto.setUpsellitemsConfiguration(mockItemDto());
    return Mono.just(bookingInformationDto);
  }

  private PrivacyPolicyDto mockPrivacyPolicyDto() {
    PrivacyPolicyDto privacyPolicy = new PrivacyPolicyDto();
    privacyPolicy.setDescription(
        "We need to collect and keep some mandatory information in order to process your booking.");
    privacyPolicy.setTitle("We keep your personal data safe and secure.");
    privacyPolicy
        .setLinkPath("/content/pi/websites/desktop/gb/en/secured/terms/privacy-policy.html");
    privacyPolicy.setLinkLabel("View our Privacy Notice");
    privacyPolicy.setMoreInfo(Collections.singletonList(InfoDto.builder()
        .image("/content/dam/global/booking/verisign.png")
        .description("test description")
        .build()));
    privacyPolicy.setMoreInfoLabel("Find Out More");

    return privacyPolicy;
  }

  private BookingInformationRequestAemDto createbookingInformationRequest() {
    return BookingInformationRequestAemDto.builder()
        .country("gb")
        .language("en")
        .bookingFlowId("booking-a1")
        .build();
  }

  private RateInformationRequestAemDto createRateInformationRequest() {
    return RateInformationRequestAemDto.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }

  private Mono<AemRateInformationDto> mockAemRateInformationDtoResponse() {
    AemRateInformationDto rateInformationDto = new AemRateInformationDto();

    RateClassificationDto rateClassificationDto = RateClassificationDto.builder()
        .rateClassification("FLEXRATE")
        .rateOrder("1")
        .rateName("Flex")
        .rateDescription("Pay now or on arrival, fully refundable with free cancellation up to 1pm")
        .rateLongDescription("")
        .build();

    rateInformationDto.setRateClassifications(Collections.singletonList(rateClassificationDto));

    return Mono.just(rateInformationDto);
  }

  private List<ItemDto> mockItemDto() {
    return List.of(ItemDto.builder()
        .code("CODE")
        .bartCode("BARTID")
        .order("3")
        .build());
  }


  private void initWebClient() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }
}
