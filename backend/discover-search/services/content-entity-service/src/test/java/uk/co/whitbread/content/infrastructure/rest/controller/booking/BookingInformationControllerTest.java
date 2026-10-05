package uk.co.whitbread.content.infrastructure.rest.controller.booking;

import static java.util.List.of;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.booking.in.BookingInformationRequest;
import uk.co.whitbread.content.domain.model.booking.in.RateInformationRequest;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.domain.model.booking.out.Item;
import uk.co.whitbread.content.domain.model.booking.out.PrivacyPolicy;
import uk.co.whitbread.content.domain.model.booking.out.RateClassification;
import uk.co.whitbread.content.domain.model.booking.out.RateInformation;
import uk.co.whitbread.content.domain.ports.primary.BookingInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper.BookingInformationDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper.BookingInformationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper.RateInformationDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.mapper.RateInformationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in.BookingInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in.RateInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.BookingInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.PrivacyPolicyDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.RateClassificationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out.RateInformationDto;


@ExtendWith(MockitoExtension.class)
class BookingInformationControllerTest {

  @InjectMocks
  private BookingInformationController bookingInformationControllerUnderTest;
  @Mock
  private BookingInPort bookingInPort;
  @Mock
  private BookingInformationRequestDtoMapper bookingInformationRequestDtoMapper;
  @Mock
  private BookingInformationDtoMapper bookingDtoMapper;
  @Mock
  private RateInformationRequestDtoMapper rateInformationRequestDtoMapper;
  @Mock
  private RateInformationDtoMapper rateInformationDtoMapper;


  @Test
  void getBookingInformation__ShouldReturnOK() {
    //Arrange
    var bookingInformationRequestDto = getBookingInformationRequestDtoGbEn();
    var bookingInformationRequest = getBookingInformationRequestGbEn();
    Mockito.when(bookingInformationRequestDtoMapper.toDomainModel(bookingInformationRequestDto))
        .thenReturn(bookingInformationRequest);
    Mockito.when(bookingInPort.getBookingInformation(bookingInformationRequest))
        .thenReturn(getBookingInformation());
    Mockito.when(bookingDtoMapper.toDto(getBookingInformation()))
        .thenReturn(getBookingInformationDto());

    //act
    var request = bookingInformationRequestDtoMapper.toDomainModel(bookingInformationRequestDto);
    var bookingInformationDto = bookingDtoMapper.toDto(
        bookingInPort.getBookingInformation(request));
    final ResponseEntity<BookingInformationDto> response = bookingInformationControllerUnderTest.getBookingInformation(
        bookingInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), bookingInformationDto.getMealsNotAvailableTitle(),
        response.getBody().getMealsNotAvailableTitle());
    assertEquals(response.toString(), bookingInformationDto.getMealsNotAvailableMessage(),
        response.getBody().getMealsNotAvailableMessage());
    assertEquals(response.toString(), bookingInformationDto.getRestaurantClosedTitle(),
        response.getBody().getRestaurantClosedTitle());
    assertEquals(response.toString(), bookingInformationDto.getRestaurantClosedMessage(),
        response.getBody().getRestaurantClosedMessage());
    assertEquals(response.toString(), bookingInformationDto.getUpsellItems(),
        response.getBody().getUpsellItems());
  }

  @Test
  void getBookingInformation__ShouldNotFindBooking() {
    //Arrange
    var bookingInformationRequestDtoGbEn = getBookingInformationRequestDtoGbEn();
    var bookingInformationRequestDtoDeDe = getBookingInformationRequestDtoDeDe();
    var bookingInformationRequest = getBookingInformationRequestGbEn();
    Mockito.when(bookingInformationRequestDtoMapper.toDomainModel(bookingInformationRequestDtoGbEn))
        .thenReturn(bookingInformationRequest);
    Mockito.when(bookingInPort.getBookingInformation(bookingInformationRequest))
        .thenReturn(getBookingInformation());
    Mockito.when(bookingDtoMapper.toDto(getBookingInformation()))
        .thenReturn(getBookingInformationDto());

    //act
    var request = bookingInformationRequestDtoMapper.toDomainModel(
        bookingInformationRequestDtoGbEn);
    var bookingInformationDto = bookingDtoMapper.toDto(
        bookingInPort.getBookingInformation(request));
    final ResponseEntity<BookingInformationDto> response = bookingInformationControllerUnderTest.getBookingInformation(
        bookingInformationRequestDtoDeDe);

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertNull(response.getBody());
  }


  @Test
  void getRateInformation__ShouldReturnOK() {
    //Arrange
    var rateInformationRequestDto = getRateInformationRequestDtoGbEn();
    var rateInformationRequest = getRateInformationRequestGbEn();
    Mockito.when(rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto))
        .thenReturn(rateInformationRequest);
    Mockito.when(bookingInPort.getRateInformation(rateInformationRequest))
        .thenReturn(getRateInformation());
    Mockito.when(rateInformationDtoMapper.toDto(getRateInformation()))
        .thenReturn(getRateInformationDto());

    //act
    var request = rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto);
    var rateInformationDto = rateInformationDtoMapper.toDto(
        bookingInPort.getRateInformation(request));
    final ResponseEntity<RateInformationDto> response = bookingInformationControllerUnderTest.getRateInformation(
        rateInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(),
        rateInformationDto.getRateClassifications().get(0).getRateClassification(),
        response.getBody().getRateClassifications().get(0).getRateClassification());
    assertEquals(response.toString(),
        rateInformationDto.getRateClassifications().get(0).getRateOrder(),
        response.getBody().getRateClassifications().get(0).getRateOrder());
    assertEquals(response.toString(),
        rateInformationDto.getRateClassifications().get(0).getRateName(),
        response.getBody().getRateClassifications().get(0).getRateName());
    assertEquals(response.toString(),
        rateInformationDto.getRateClassifications().get(0).getRateDescription(),
        response.getBody().getRateClassifications().get(0).getRateDescription());
  }

  @Test
  void getRateInformation__ShouldNotFinRateInformation() {
    //Arrange
    var rateInformationRequestDto = getRateInformationRequestDtoGbEn();
    var rateInformationRequest = getRateInformationRequestGbEn();
    Mockito.when(rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto))
        .thenReturn(rateInformationRequest);
    Mockito.when(bookingInPort.getRateInformation(rateInformationRequest))
        .thenReturn(getRateInformation());
    Mockito.when(rateInformationDtoMapper.toDto(getRateInformation()))
        .thenReturn(getRateInformationDto());

    //act
    var request = rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto);
    var rateInformationDto = rateInformationDtoMapper.toDto(
        bookingInPort.getRateInformation(request));
    final ResponseEntity<RateInformationDto> response = bookingInformationControllerUnderTest.getRateInformation(
        rateInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(),
        rateInformationDto.getRateClassifications().get(0).getRateClassification(),
        response.getBody().getRateClassifications().get(0).getRateClassification());
    assertEquals(response.toString(),
        rateInformationDto.getRateClassifications().get(0).getRateOrder(),
        response.getBody().getRateClassifications().get(0).getRateOrder());
    assertEquals(response.toString(),
        rateInformationDto.getRateClassifications().get(0).getRateName(),
        response.getBody().getRateClassifications().get(0).getRateName());
    assertEquals(response.toString(),
        rateInformationDto.getRateClassifications().get(0).getRateDescription(),
        response.getBody().getRateClassifications().get(0).getRateDescription());

  }

  @Test
  void getHotelRateInformation__ShouldReturnOK() {
    //Arrange
    var rateInformationRequestDto = getRateInformationRequestDtoGbEn();
    var rateInformationRequest = getRateInformationRequestGbEn();
    Mockito.when(rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto))
            .thenReturn(rateInformationRequest);
    Mockito.when(bookingInPort.getHotelRateInformation(rateInformationRequest))
            .thenReturn(getRateInformation());
    Mockito.when(rateInformationDtoMapper.toDto(getRateInformation()))
            .thenReturn(getRateInformationDto());

    //act
    var request = rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto);
    var rateInformationDto = rateInformationDtoMapper.toDto(
            bookingInPort.getHotelRateInformation(request));
    final ResponseEntity<RateInformationDto> response = bookingInformationControllerUnderTest.getHotelRateInformation(
            rateInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(),
            rateInformationDto.getRateClassifications().get(0).getRateClassification(),
            response.getBody().getRateClassifications().get(0).getRateClassification());
    assertEquals(response.toString(),
            rateInformationDto.getRateClassifications().get(0).getRateOrder(),
            response.getBody().getRateClassifications().get(0).getRateOrder());
    assertEquals(response.toString(),
            rateInformationDto.getRateClassifications().get(0).getRateName(),
            response.getBody().getRateClassifications().get(0).getRateName());
    assertEquals(response.toString(),
            rateInformationDto.getRateClassifications().get(0).getRateDescription(),
            response.getBody().getRateClassifications().get(0).getRateDescription());
  }

  @Test
  void getHotelRateInformation__ShouldNotFinHotelRateInformation() {
    //Arrange
    var rateInformationRequestDto = getRateInformationRequestDtoGbEn();
    var rateInformationRequest = getRateInformationRequestGbEn();
    Mockito.when(rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto))
            .thenReturn(rateInformationRequest);
    Mockito.when(bookingInPort.getHotelRateInformation(rateInformationRequest))
            .thenReturn(getRateInformation());
    Mockito.when(rateInformationDtoMapper.toDto(getRateInformation()))
            .thenReturn(getRateInformationDto());

    //act
    var request = rateInformationRequestDtoMapper.toDomainModel(rateInformationRequestDto);
    var rateInformationDto = rateInformationDtoMapper.toDto(
            bookingInPort.getHotelRateInformation(request));
    final ResponseEntity<RateInformationDto> response = bookingInformationControllerUnderTest.getHotelRateInformation(
            rateInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(),
            rateInformationDto.getRateClassifications().get(0).getRateClassification(),
            response.getBody().getRateClassifications().get(0).getRateClassification());
    assertEquals(response.toString(),
            rateInformationDto.getRateClassifications().get(0).getRateOrder(),
            response.getBody().getRateClassifications().get(0).getRateOrder());
    assertEquals(response.toString(),
            rateInformationDto.getRateClassifications().get(0).getRateName(),
            response.getBody().getRateClassifications().get(0).getRateName());
    assertEquals(response.toString(),
            rateInformationDto.getRateClassifications().get(0).getRateDescription(),
            response.getBody().getRateClassifications().get(0).getRateDescription());

  }

  private BookingInformation getBookingInformation() {
    return BookingInformation.builder()
        .restaurantClosedTitle("Restaurant unavailable")
        .restaurantClosedMessage(
            "We're sorry, the restaurant at this hotel is closed on your selected dates.")
        .mealsNotAvailableTitle("Important restaurant information")
        .mealsNotAvailableMessage(
            "We're sorry, the restaurant at this hotel isn't serving breakfasts or Meal Deals on the dates you've selected.")
        .infoMessages(Collections.emptyList())
        .privacyPolicy(PrivacyPolicy.builder().build())
        .termsAndConditions(Collections.emptyList())
        .promotionPanels(Collections.emptyList())
        .upsellItems(mockItem())
        .build();
  }

  private BookingInformationRequestDto getBookingInformationRequestDtoGbEn() {
    return BookingInformationRequestDto.builder()
        .bookingFlowId("booking")
        .country("gb")
        .language("en")
        .build();
  }

  private BookingInformationRequestDto getBookingInformationRequestDtoDeDe() {
    return BookingInformationRequestDto.builder()
        .bookingFlowId("booking")
        .country("de")
        .language("de")
        .build();
  }

  private BookingInformationRequest getBookingInformationRequestGbEn() {
    return BookingInformationRequest.builder()
        .bookingFlowId("booking")
        .country("gb")
        .language("en")
        .hotelId("DUBSOU")
        .build();
  }

  private RateInformationRequestDto getRateInformationRequestDtoGbEn() {
    return RateInformationRequestDto.builder()
        .brand("pi")
        .country("gb")
        .language("en")
        .hotelId("DUBSOU")
        .build();
  }

  private RateInformationRequest getRateInformationRequestGbEn() {
    return RateInformationRequest.builder()
        .brand("pi")
        .country("de")
        .language("de")
        .build();
  }

  private BookingInformationDto getBookingInformationDto() {
    return BookingInformationDto.builder()
        .restaurantClosedTitle("Restaurant unavailable")
        .restaurantClosedMessage(
            "We're sorry, the restaurant at this hotel is closed on your selected dates.")
        .mealsNotAvailableTitle("Important restaurant information")
        .mealsNotAvailableMessage(
            "We're sorry, the restaurant at this hotel isn't serving breakfasts or Meal Deals on the dates you've selected.")
        .infoMessages(Collections.emptyList())
        .privacyPolicy(PrivacyPolicyDto.builder().build())
        .termsAndConditions(Collections.emptyList())
        .promotionPanels(Collections.emptyList())
        .build();
  }

  private RateInformation getRateInformation() {
    return RateInformation.builder()
        .rateClassifications(of(RateClassification.builder()
                .rateClassification("A")
                .rateOrder("1")
                .rateName("Flex")
                .rateDescription(
                    "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival.")
                .rateLongDescription("")
                .rateNotes("")
                .build(),
            RateClassification.builder()
                .rateClassification("FLEXRATE")
                .rateOrder("1")
                .rateName("Flex")
                .rateDescription(
                    "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival.")
                .rateLongDescription("")
                .rateNotes("")
                .build(),
            RateClassification.builder()
                .rateClassification("SEMIFLEX")
                .rateOrder("3")
                .rateName("Semi-Flex")
                .rateDescription(
                    "Pay now, fully refundable with free cancellation up to 3 full days before arrival")
                .rateLongDescription("")
                .rateNotes("")
                .build()))
        .build();
  }

  private RateInformationDto getRateInformationDto() {
    return RateInformationDto.builder()
        .rateClassifications(of(RateClassificationDto.builder()
                .rateClassification("A")
                .rateOrder("1")
                .rateName("Flex")
                .rateDescription(
                    "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival.")
                .rateLongDescription("")
                .rateNotes("")
                .build(),
            RateClassificationDto.builder()
                .rateClassification("FLEXRATE")
                .rateOrder("1")
                .rateName("Flex")
                .rateDescription(
                    "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival.")
                .rateLongDescription("")
                .rateNotes("")
                .build(),
            RateClassificationDto.builder()
                .rateClassification("SEMIFLEX")
                .rateOrder("3")
                .rateName("Semi-Flex")
                .rateDescription(
                    "Pay now, fully refundable with free cancellation up to 3 full days before arrival")
                .rateLongDescription("")
                .rateNotes("")
                .build()))
        .build();
  }

  private List<Item> mockItem() {
    return List.of(Item.builder()
            .code("CODE")
            .bartId("BARTID")
            .order("3")
            .build());
  }
}


