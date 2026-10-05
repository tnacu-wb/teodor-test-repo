package uk.co.whitbread.booking.infrastructure.rest.client.reservation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.booking.domain.model.channel.BookingChannelValues.CHANNEL_BB;
import static uk.co.whitbread.booking.domain.model.channel.BookingChannelValues.CHANNEL_PI;
import static uk.co.whitbread.booking.domain.model.channel.BookingChannelValues.SUBCHANNEL_PI;
import static uk.co.whitbread.booking.domain.model.exceptions.ErrorCode.DIGITAL_CANCEL_BOOKING_INVALID_AUTHORIZATION_EXCEPTION;
import static uk.co.whitbread.booking.domain.model.exceptions.ErrorCode.DIGITAL_GET_ACCESS_LEVEL_EXCEPTION;
import static uk.co.whitbread.booking.domain.model.exceptions.ErrorCode.DIGITAL_GET_OPERA_BOOKING_EXCEPTION;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.BaseEmailRequest;
import uk.co.whitbread.booking.domain.model.exceptions.AuthorizationException;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.in.HotelInformationRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.BookingPackagesDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingPrice;
import uk.co.whitbread.booking.domain.model.information.out.BookingRoom;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.information.out.CancellationInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.HotelInformationResponse;
import uk.co.whitbread.booking.domain.model.information.out.UpcomingBookings;
import uk.co.whitbread.booking.infrastructure.exceptions.BadRequestException;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.in.HotelInformationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.in.MealsRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.HotelInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.MealsInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.model.out.UpsellItemsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.content.service.ContentClient;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.in.PackageGroupRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.LightweightReservationByIdDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageCodesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageGroupOhipResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageGroupsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationLightweightResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions.InternalBasketException;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ContentRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ReservationRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ReservationResponseConverter;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.BaseOperaEmailRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.CancelReservationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationBookingInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationChannelDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationRateInformationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.CancelReservationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationAllowanceDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationAllowancesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationBasketOptionsResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationBillingDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationCancelInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationGuestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationPackagesDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRateClasificationsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRateInformationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRatePerNightDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRoomDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class ReservationOutPortImplTest {

  private static final String AUTHORIZATION = "Bearer authorization";
  private static final String BOOKING_REFERENCE = "bookingReference";
  private static final String BASKET_REFERENCE = "basketReference";
  private static final String HOTEL_ID = "hotelId";
  private static final String SURNAME = "surname";
  private static final String ARRIVAL_DATE = "arrival";
  private static final String RESERVATION_CHECKOUT_TIME = "12:55";
  private static final String RESERVATION_ARRIVAL_DATE = "2023-03-12";
  private static final String RESERVATION_DEPARTURE_DATE = "2023-03-14";
  private static final String PAYMENT_OPTION = "paymentOption";
  private static final String BOOKING_STATUS = "bookingStatus";
  private static final String BOOKING_SUBCHANNEL = "bookingSubchannel";
  private static final String EMAIL = "email";
  private static final String GUEST = "STAYER";

  private static final String FI24HR = "FI24HR";


  @Mock
  private ReservationClient reservationClient;
  @Mock
  private BasketClient basketClient;
  @Mock
  private ContentClient contentClient;

  @Mock
  private ReservationResponseConverter reservationResponseMapper;
  @Mock
  private ReservationRequestMapper reservationRequestMapper;
  @Mock
  private ContentRequestMapper contentRequestMapper;
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private OhipAdapterClient ohipAdapterClient;

  @Spy
  @InjectMocks
  ReservationOutPortImpl outService;


  @Test
  void getBookingInformationAuth_noBookingInformationFound() {

    // Arrange
    given(reservationClient.getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE)).willReturn(
        reservationResponse());

    given(reservationRequestMapper.toDto(bookingChannelBB())).willReturn(
        reservationChannelBBDto());
    given(reservationRequestMapper.toModel(bookingInfoRequest(), reservationChannelBBDto().getChannel())).willReturn(
        reservationRateInformationBBRequest());
    given(authenticatedUserService.isUserAuthenticated()).willReturn(true);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(new CustomJwtAuthenticationToken(getJwt(),
        getAccount()));

    // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelBB());

    // Assert
    assertThat(result, nullValue());
    verify(reservationClient).getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE);
    verify(reservationRequestMapper).toModel(bookingInfoRequest(), reservationChannelBBDto().getChannel());
  }

  @Test
  void getBookingInformationAuth_bookingInformationFound() {

    // Arrange
    var packageGroupRespone = packageGroupsResponse();
    given(reservationClient.getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE)).willReturn(
        reservationResponse());
    given(basketClient.getBasketOptions(BASKET_REFERENCE)).willReturn(
        basketOptionsResponse());
    given(contentClient.getRateInformation(reservationRateInformationBBRequest())).willReturn(
        rateInformationResponse());
    given(reservationClient.getDinnerAllowances(BASKET_REFERENCE)).willReturn(
        reservationAllowancesDto());
    given(authenticatedUserService.isUserAuthenticated()).willReturn(true);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(new CustomJwtAuthenticationToken(getJwt(),
        getAccount()));
    given(reservationRequestMapper.toDto(bookingChannelBB())).willReturn(
        reservationChannelBBDto());
    given(
        reservationRequestMapper.toModel(bookingInfoRequest(), reservationChannelBBDto().getChannel())).willReturn(
        reservationRateInformationBBRequest());
    given(ohipAdapterClient.getPackageGroups(any(PackageGroupRequestDto.class)))
        .willReturn(packageGroupRespone);

    given(reservationResponseMapper.toModel(reservationResponse(), basketOptionsResponse(),
        rateInformationResponse(), cancelInfoResponse(), bookingInfoRequest().getBookingReference(),
        reservationAllowancesDto(), packageGroupRespone.getPackagesGroup(), BigDecimal.ZERO)).willReturn(
        bookingDetails());
    given(reservationResponseMapper.toModel(reservationResponse(), bookingDetails())).willReturn(
        bookingInfoResponse());
    when(contentRequestMapper.toModel(bookingInfoRequest()))
        .thenReturn(aemMealsInformationPIRequest());
    given(contentClient.getMealsInformation(aemMealsInformationPIRequest())).willReturn(
        aemMealsInformationResponse());
    Set<BookingPackagesDetails> adultsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(room -> room.getAdultsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(adultsMeal,aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
    Set<BookingPackagesDetails> kidsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(room -> room.getKidsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(kidsMeal,aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
    // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelBB());

    // Assert
    assertThat(result, notNullValue());
    verify(reservationClient).getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE);
    verify(basketClient).getBasketOptions(BASKET_REFERENCE);
    verify(contentClient).getRateInformation(reservationRateInformationBBRequest());
    verify(reservationClient).getDinnerAllowances(BASKET_REFERENCE);
    verify(reservationRequestMapper).toModel(bookingInfoRequest(), reservationChannelBBDto().getChannel());
  }

  @Test
  void getBookingInformationAuth_bookingInformationFound_wifiExtrasAvailable() {
    var bookingDetails = bookingDetailsWithExtras();
    var bookingDetailsInfoResponse = bookingDetailsWithExtras();

    // set total price for wifi exras
    bookingDetailsInfoResponse.getRooms().get(0).getExtrasItems().stream()
            .findFirst().get().getTotalPrice().setAmount(new BigDecimal(10));

    var packageGroupResponse = packageGroupsResponse();
    // Arrange
    given(reservationClient.getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE)).willReturn(
            reservationResponse());
    given(basketClient.getBasketOptions(BASKET_REFERENCE)).willReturn(
            basketOptionsResponse());
    given(contentClient.getRateInformation(reservationRateInformationBBRequest())).willReturn(
            rateInformationResponse());
    given(reservationClient.getDinnerAllowances(BASKET_REFERENCE)).willReturn(
            reservationAllowancesDto());
    given(authenticatedUserService.isUserAuthenticated()).willReturn(true);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(new CustomJwtAuthenticationToken(getJwt(),
            getAccount()));
    given(reservationRequestMapper.toDto(bookingChannelBB())).willReturn(
            reservationChannelBBDto());
    given(
            reservationRequestMapper.toModel(bookingInfoRequest(), reservationChannelBBDto().getChannel())).willReturn(
            reservationRateInformationBBRequest());
    given(ohipAdapterClient.getPackageGroups(any(PackageGroupRequestDto.class)))
        .willReturn(packageGroupResponse);

    given(reservationResponseMapper.toModel(reservationResponse(), basketOptionsResponse(),
        rateInformationResponse(), cancelInfoResponse(), bookingInfoRequest().getBookingReference(),
        reservationAllowancesDto(), packageGroupResponse.getPackagesGroup(), BigDecimal.ZERO)).willReturn(
        bookingDetails);


    var bookingInfoResponse = bookingInfoResponse();
    bookingInfoResponse.setReservationDetails(bookingDetailsInfoResponse);

    given(reservationResponseMapper.toModel(reservationResponse(), bookingDetailsInfoResponse)).willReturn(
            bookingInfoResponse);
    when(contentRequestMapper.toModel(bookingInfoRequest()))
            .thenReturn(aemMealsInformationPIRequest());
    given(contentClient.getMealsInformation(aemMealsInformationPIRequest())).willReturn(
            aemMealsInformationResponse());
    Set<BookingPackagesDetails> adultsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(room -> room.getAdultsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(adultsMeal,aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
    Set<BookingPackagesDetails> kidsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(room -> room.getKidsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(kidsMeal,aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
    // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelBB());

    // Assert
    assertThat(result, notNullValue());
    verify(reservationClient).getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE);
    verify(basketClient).getBasketOptions(BASKET_REFERENCE);
    verify(contentClient).getRateInformation(reservationRateInformationBBRequest());
    verify(reservationClient).getDinnerAllowances(BASKET_REFERENCE);
    verify(reservationRequestMapper).toModel(bookingInfoRequest(), reservationChannelBBDto().getChannel());
  }

  @Test
  void getBookingInformationAuth_bookingInformationFound_withCityTax() {
    // Arrange
    var reservationWithCityTax = reservationResponseWithCityTax();
    var expectedCityTaxAmount = new BigDecimal("6.00"); // 2 nights x 3.00 per night
    var packageGroupResponse = packageGroupsResponse();

    given(reservationClient.getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE)).willReturn(
            reservationWithCityTax);
    given(basketClient.getBasketOptions(BASKET_REFERENCE)).willReturn(
            basketOptionsResponse());
    given(contentClient.getRateInformation(reservationRateInformationBBRequest())).willReturn(
            rateInformationResponse());
    given(reservationClient.getDinnerAllowances(BASKET_REFERENCE)).willReturn(
            reservationAllowancesDto());
    given(authenticatedUserService.isUserAuthenticated()).willReturn(true);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(new CustomJwtAuthenticationToken(getJwt(),
            getAccount()));
    given(reservationRequestMapper.toDto(bookingChannelBB())).willReturn(
            reservationChannelBBDto());
    given(reservationRequestMapper.toModel(bookingInfoRequest(), reservationChannelBBDto().getChannel())).willReturn(
            reservationRateInformationBBRequest());
    given(ohipAdapterClient.getPackageGroups(any(PackageGroupRequestDto.class)))
        .willReturn(packageGroupResponse);

    given(reservationResponseMapper.toModel(reservationWithCityTax, basketOptionsResponse(),
        rateInformationResponse(), cancelInfoResponse(), bookingInfoRequest().getBookingReference(),
        reservationAllowancesDto(), packageGroupResponse.getPackagesGroup(), expectedCityTaxAmount)).willReturn(
        bookingDetails());
    given(reservationResponseMapper.toModel(reservationWithCityTax, bookingDetails())).willReturn(
        bookingInfoResponse());
    when(contentRequestMapper.toModel(bookingInfoRequest()))
        .thenReturn(aemMealsInformationPIRequest());
    given(contentClient.getMealsInformation(aemMealsInformationPIRequest())).willReturn(
        aemMealsInformationResponse());
    Set<BookingPackagesDetails> adultsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(BookingRoom::getAdultsMeal).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(adultsMeal, aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
    Set<BookingPackagesDetails> kidsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(BookingRoom::getKidsMeal).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(kidsMeal, aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));

    // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelBB());

    // Assert
    assertThat(result, notNullValue());
    verify(reservationResponseMapper).toModel(reservationWithCityTax, basketOptionsResponse(),
        rateInformationResponse(), cancelInfoResponse(), bookingInfoRequest().getBookingReference(),
        reservationAllowancesDto(), packageGroupResponse.getPackagesGroup(), expectedCityTaxAmount);
  }

  @Test
  void getBookingInformation_emptyBookingInfoRequest() {
    final BookingChannel piChannel = bookingChannelPI();
    given(reservationRequestMapper.toDto(piChannel)).willReturn(
        reservationChannelPIDto());
    var bookingInfoRequest = emptyBookingInfoRequest();
    Exception exception = assertThrows(BadRequestException.class, () -> {
      outService.getOperaBookingInformation(bookingInfoRequest, piChannel);
    });
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains("Booking info request does not contain"));
  }

  @Test
  void findBooking() {
    // Arrange
    var bookingChannel = bookingChannelPI();
    var bookingInfoRequest = bookingInfoRequest();
    var isPastBooking = true;
    var bookingInfoRequestDto = ReservationBookingInfoRequestDto.builder().build();

    given(reservationClient.findBooking(bookingInfoRequestDto)).willReturn(
        reservationResponse());
    given(reservationRequestMapper.toDto(bookingInfoRequest,bookingChannel, isPastBooking)).willReturn(
        bookingInfoRequestDto);

    // Act
    outService.findBooking(bookingInfoRequest, bookingChannel, isPastBooking);

    // Assert
    verifyNoMoreInteractions(reservationClient);
    verifyNoMoreInteractions(reservationRequestMapper);
  }

  @Test
  void getBookingInformation_noBookingInformationFound() {
    // Arrange
    given(basketClient.getBasketByBookingReference(BOOKING_REFERENCE)).willReturn(
        basketOptionsResponse());
    given(reservationClient.getReservationInformation(BASKET_REFERENCE)).willReturn(
        reservationResponse());

    given(reservationRequestMapper.toDto(bookingChannelPI())).willReturn(
        reservationChannelPIDto());
    given(reservationRequestMapper.toModel(bookingInfoRequest(), reservationChannelPIDto().getChannel())).willReturn(
        reservationRateInformationPIRequest());

    // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelPI());

    // Assert
    assertThat(result, nullValue());
    verify(basketClient).getBasketByBookingReference(BOOKING_REFERENCE);
    verify(reservationClient).getReservationInformation(BASKET_REFERENCE);
    verify(reservationRequestMapper).toModel(bookingInfoRequest(), reservationChannelPIDto().getChannel());
  }

  @Test
  void getBookingInformation__bookingInformationFound() {

    // Arrange
    var packageGroupsResponse = packageGroupsResponse();
    given(reservationClient.getReservationInformation(BASKET_REFERENCE)).willReturn(
        reservationResponse());
    given(basketClient.getBasketByBookingReference(BOOKING_REFERENCE)).willReturn(
        basketOptionsResponse());
    given(contentClient.getRateInformation(reservationRateInformationPIRequest())).willReturn(
        rateInformationResponse());
    given(reservationClient.getDinnerAllowances(BASKET_REFERENCE)).willReturn(
        reservationAllowancesDto());
    given(reservationRequestMapper.toDto(bookingChannelPI())).willReturn(
        reservationChannelPIDto());
    given(
        reservationRequestMapper.toModel(bookingInfoRequest(), reservationChannelPIDto().getChannel())).willReturn(
        reservationRateInformationPIRequest());
    given(ohipAdapterClient.getPackageGroups(any(PackageGroupRequestDto.class)))
        .willReturn(packageGroupsResponse);

    given(reservationResponseMapper.toModel(reservationResponse(), basketOptionsResponse(),
        rateInformationResponse(), cancelInfoResponse(), bookingInfoRequest().getBookingReference(),
        reservationAllowancesDto(), packageGroupsResponse.getPackagesGroup(), BigDecimal.ZERO)).willReturn(
        bookingDetails());
    given(reservationResponseMapper.toModel(reservationResponse(), bookingDetails())).willReturn(
        bookingInfoResponse());
    when(contentRequestMapper.toModel(bookingInfoRequest()))
        .thenReturn(aemMealsInformationPIRequest());
    given(contentClient.getMealsInformation(aemMealsInformationPIRequest())).willReturn(
        aemMealsInformationResponse());
    Set<BookingPackagesDetails> adultsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(room -> room.getAdultsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(adultsMeal,aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
    Set<BookingPackagesDetails> kidsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(room -> room.getKidsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(kidsMeal,aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
    // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelPI());

    // Assert
    assertThat(result, notNullValue());
    verify(reservationClient).getReservationInformation(BASKET_REFERENCE);
    verify(basketClient).getBasketByBookingReference(BOOKING_REFERENCE);
    verify(contentClient).getRateInformation(reservationRateInformationPIRequest());
    verify(reservationRequestMapper).toModel(bookingInfoRequest(), reservationChannelPIDto().getChannel());
  }

  @Test
  void getBookingInformation__bookingInformationFoundDisallowCancel() {

    // Arrange
    var packageGroupsResponse = packageGroupsResponse();
    given(authenticatedUserService.isUserAuthenticated()).willReturn(true);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(new CustomJwtAuthenticationToken(getJwt(),
            getAccount()));
    given(reservationClient.getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE)).willReturn(
            reservationResponse());
    given(basketClient.getBasketOptions(BASKET_REFERENCE)).willReturn(
            basketOptionsResponse());
    given(contentClient.getRateInformation(reservationRateInformationBBRequest())).willReturn(
            rateInformationResponse());
    given(reservationClient.getDinnerAllowances(BASKET_REFERENCE)).willReturn(
            reservationAllowancesDto());
    given(reservationRequestMapper.toDto(bookingChannelBB())).willReturn(
            reservationChannelBBDto());
    given(reservationRequestMapper.toModel(bookingInfoRequest(), reservationChannelBBDto().getChannel())).willReturn(
            reservationRateInformationBBRequest());

    doReturn(cancelInfoResponseCancelable()).when(outService).retrieveCancelResponse(bookingInfoRequest(), reservationChannelBBDto(), reservationResponse());
    doReturn(GUEST).when(outService).getAccessLevel();
    given(ohipAdapterClient.getPackageGroups(any(PackageGroupRequestDto.class)))
        .willReturn(packageGroupsResponse);
    given(reservationResponseMapper.toModel(reservationResponse(), basketOptionsResponse(),
        rateInformationResponse(), cancelInfoNotCancelAmendInfoResponse(),
        bookingInfoRequest().getBookingReference(),
        reservationAllowancesDto(), packageGroupsResponse.getPackagesGroup(), BigDecimal.ZERO)).willReturn(
        bookingDetails());
    given(reservationResponseMapper.toModel(reservationResponse(), bookingDetails())).willReturn(
               bookingInfoResponse());
    when(contentRequestMapper.toModel(bookingInfoRequest()))
        .thenReturn(aemMealsInformationPIRequest());
    given(contentClient.getMealsInformation(aemMealsInformationPIRequest())).willReturn(
        aemMealsInformationResponse());
    Set<BookingPackagesDetails> adultsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(room -> room.getAdultsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(adultsMeal,aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
    Set<BookingPackagesDetails> kidsMeal = bookingInfoResponse().getReservationDetails().getRooms().stream().map(room -> room.getKidsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(kidsMeal,aemMealsInformationResponse())).thenReturn(Set.of(packageDetails()));
       // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelBB());

      // Assert
    assertThat(result, notNullValue());
    Matchers.is(!result.getReservationDetails().getCancellationInfoResponse().getCancelable());
    verify(reservationClient).getReservationInformationAuth(AUTHORIZATION, BOOKING_REFERENCE);
  }

  @Test
  void getBookingInformation__notEnoughParamsException() {
    var bookingInfo = bookingInfoRequestNotComplete();
    var bookingChannel = bookingChannelPI();
    given(reservationRequestMapper.toDto(bookingChannelPI())).willReturn(
            reservationChannelPIDto());

    // Act & Assert
    var result = Assertions.assertThrows(BadRequestException.class,
        () -> outService.getOperaBookingInformation(bookingInfo, bookingChannel));
    assertTrue(result.getMessage().contains("Please supply required fields for search"));
  }

  @Test
  void getOperaBookingInformation_throwsBadRequestException() {
    // Arrange
      var bookingInfo = bookingInfoRequestNotComplete();
      var bookingChannel = bookingChannelPI();
    given(reservationRequestMapper.toDto(bookingChannelPI())).willReturn(reservationChannelPIDto());

    // Act & Assert
    var exception = assertThrows(BadRequestException.class,
        () -> outService.getOperaBookingInformation(bookingInfo, bookingChannel));
    assertEquals(DIGITAL_GET_OPERA_BOOKING_EXCEPTION.getCode(), exception.getErrorCode());
  }


  @Test
  void accessLevel_fail() {
    String NOT_AUTHORIZED = "Not authorized";
    given(authenticatedUserService.isUserAuthenticated()).willReturn(false);
    AuthorizationException response = Assertions.assertThrows(AuthorizationException.class, ()->outService.getAccessLevel());
    assertTrue(response.getMessage().contains(NOT_AUTHORIZED));
    assertEquals(DIGITAL_GET_ACCESS_LEVEL_EXCEPTION.getCode(), response.getErrorCode());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getTokenTest(boolean authenticated) {
    // Arrange
    given(authenticatedUserService.isUserAuthenticated()).willReturn(authenticated);
    if (authenticated) {
      given(authenticatedUserService.getAuthenticatedUser()).willReturn(
          new CustomJwtAuthenticationToken(getJwt(), getAccount()));
    }
    // Act
    if (!authenticated) {
      var exception = assertThrows(AuthorizationException.class,
          () -> {
            ReflectionTestUtils.invokeMethod(outService, "getToken");
          });
      assertEquals(ErrorCode.DIGITAL_GET_TOKEN_EXCEPTION.getCode(), exception.getErrorCode());
    } else {
      String result = ReflectionTestUtils.invokeMethod(outService, "getToken");
      //Assert
      assertNotNull(result);
    }
  }
  @Test
  void cancelReservation_success() {
    var cancelBookingRequest = CancelBookingRequest.builder().build();
    var cancelReservationRequest = CancelReservationRequestDto.builder().build();
    var cancelReservationResponse = CancelReservationResponseDto.builder().build();
    given(reservationRequestMapper.toDto(cancelBookingRequest)).willReturn(
        cancelReservationRequest);
    given(authenticatedUserService.getAuthenticatedUser()).willReturn(new CustomJwtAuthenticationToken(getJwt(),
        getAccount()));
    given(reservationClient.cancelReservation(AUTHORIZATION, cancelReservationRequest)).willReturn(
        cancelReservationResponse);
    given(reservationResponseMapper.toModel(cancelReservationResponse)).willReturn(cancelBookingResponse());

    var response = outService.cancelBooking(cancelBookingRequest);

    assertThat(response.getBookingReference(), notNullValue());
  }

  @Test
  void cancelReservation_throwsAuthorizationException() {
    //Arrange
    var cancelBookingRequest = CancelBookingRequest.builder().build();
    var cancelReservationRequest = CancelReservationRequestDto.builder().build();
    var exception = new AuthorizationException(
        DIGITAL_CANCEL_BOOKING_INVALID_AUTHORIZATION_EXCEPTION,
        "Not authorized");
    given(reservationRequestMapper.toDto(cancelBookingRequest)).willReturn(
        cancelReservationRequest);
    given(authenticatedUserService.getAuthenticatedUser()).willThrow(exception);

    //Act & Assert
    var actual = assertThrows(AuthorizationException.class,
        () -> outService.cancelBooking(cancelBookingRequest));
    assertEquals(exception, actual);
  }

  @Test
  void sendBookingConfirmationOrInvoiceEmail__success() {
    given(reservationRequestMapper.toDto(baseEmailRequest())).willReturn(
        baseOperaEmailRequest());
    given(basketClient.sendBookingConfirmationOrInvoiceEmail(baseOperaEmailRequest())).willReturn(
        new ResponseEntity<>(HttpStatus.ACCEPTED));

    var response = basketClient.sendBookingConfirmationOrInvoiceEmail(baseOperaEmailRequest());

    outService.sendBookingConfirmationOrInvoiceEmail(baseEmailRequest());

    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
  }

  @Test
  void sendBookingConfirmationOrInvoiceEmail__failed() {
    var baseEmailRequest = BaseEmailRequest.builder().build();
    given(reservationRequestMapper.toDto(baseEmailRequest)).willReturn(
        baseOperaEmailRequest());
    given(basketClient.sendBookingConfirmationOrInvoiceEmail(baseOperaEmailRequest()))
        .willThrow(new InternalBasketException("message", "debug message", new Exception(), 100));


    assertThrows(InternalBasketException.class,
        () -> outService.sendBookingConfirmationOrInvoiceEmail(baseEmailRequest));
  }
  
  //START -- DNRQ-54871
  @Test
  void getBookingInformation_aemMealPackage() {
    // Arrange
    var packageGroupsResponse = packageGroupsResponse();
    given(reservationClient.getReservationInformation(BASKET_REFERENCE)).willReturn(
        reservationResponse(true));
    given(basketClient.getBasketByBookingReference(BOOKING_REFERENCE)).willReturn(
        basketOptionsResponse());
    given(contentClient.getRateInformation(reservationRateInformationPIRequest())).willReturn(
        rateInformationResponse());
    given(reservationClient.getDinnerAllowances(BASKET_REFERENCE)).willReturn(
        reservationAllowancesDto());
    given(reservationRequestMapper.toDto(bookingChannelPI())).willReturn(
        reservationChannelPIDto());
    given(reservationRequestMapper.toModel(bookingInfoRequest(),
        reservationChannelPIDto().getChannel()))
        .willReturn(reservationRateInformationPIRequest());

    given(ohipAdapterClient.getPackageGroups(any(PackageGroupRequestDto.class)))
        .willReturn(packageGroupsResponse);

    when(reservationResponseMapper.toModel(reservationResponse(true),
        basketOptionsResponse(),
        rateInformationResponse(), cancelInfoResponse(), bookingInfoRequest().getBookingReference(),
        reservationAllowancesDto(), packageGroupsResponse.getPackagesGroup(), BigDecimal.ZERO)).thenReturn(
        bookingDetails(false));


    when(reservationResponseMapper.toModel(
        any(ReservationResponseDto.class),
        any(BookingDetails.class)))
        .thenReturn(bookingInfoResponse(true));

    when(reservationResponseMapper.toModel(
        reservationResponse(true),
        bookingDetails(true))).thenReturn(
        bookingInfoResponse(true));

    when(contentRequestMapper.toModel(bookingInfoRequest()))
        .thenReturn(aemMealsInformationPIRequest());

    given(contentClient.getMealsInformation(aemMealsInformationPIRequest())).willReturn(
        aemMealsInformationResponse());

    BookingDetails bookingDetails = bookingDetails(false);
    Set<BookingPackagesDetails> adultsMeal = bookingDetails.getRooms().stream().map(room -> room.getAdultsMeal()).findAny().get();
    when(reservationResponseMapper.getUpdatedMealPackages(adultsMeal,aemMealsInformationResponse())).thenReturn(updatedAdultsMealPackages());
    Set<BookingPackagesDetails> kidsMeal = bookingDetails.getRooms().stream().map(room -> room.getKidsMeal()).findAny().get();

    when(reservationResponseMapper.getUpdatedMealPackages(kidsMeal,aemMealsInformationResponse())).thenReturn(updatedKidsMealPackages());

    // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelPI());

    // Assert
    assertThat(result, notNullValue());
    BookingPackagesDetails updatedAdultMeal = getUpdatedAdultMealsPackage(result);
    assertEquals("Premier Inn Breakfast AEM", updatedAdultMeal.getDescription());
    assertEquals(BigDecimal.valueOf(20), updatedAdultMeal.getTotalPrice().getAmount());
    BookingPackagesDetails updatedKidMeal = getUpdatedKidMealsPackage(result);
    assertEquals("Free breakfast for kids AEM", updatedKidMeal.getDescription());
    assertEquals(BigDecimal.valueOf(10), updatedKidMeal.getTotalPrice().getAmount());
  }

  @Test
  void getBookingInformation_emptyMealPackage() {
    // Arrange
    var packageGroupsResponse = packageGroupsResponse();
    given(reservationClient.getReservationInformation(BASKET_REFERENCE)).willReturn(
        reservationResponse(false));
    given(basketClient.getBasketByBookingReference(BOOKING_REFERENCE)).willReturn(
        basketOptionsResponse());
    given(contentClient.getRateInformation(reservationRateInformationPIRequest())).willReturn(
        rateInformationResponse());
    given(reservationClient.getDinnerAllowances(BASKET_REFERENCE)).willReturn(
        reservationAllowancesDto());
    given(reservationRequestMapper.toDto(bookingChannelPI())).willReturn(
        reservationChannelPIDto());
    given(reservationRequestMapper.toModel(bookingInfoRequest(),
        reservationChannelPIDto().getChannel()))
        .willReturn(reservationRateInformationPIRequest());
    given(ohipAdapterClient.getPackageGroups(any(PackageGroupRequestDto.class)))
        .willReturn(packageGroupsResponse);
    when(reservationResponseMapper.toModel(reservationResponse(false),
        basketOptionsResponse(),
        rateInformationResponse(), cancelInfoResponse(), bookingInfoRequest().getBookingReference(),
        reservationAllowancesDto(), packageGroupsResponse.getPackagesGroup(), BigDecimal.ZERO)).thenReturn(
        emptyMealsBookingDetails());

    when(reservationResponseMapper.toModel(
        reservationResponse(false),
        emptyMealsBookingDetails())).thenReturn(
        emptyMealsBookingInfoResponse());

    // Act
    var result = outService.getOperaBookingInformation(bookingInfoRequest(), bookingChannelPI());

    // Assert
    assertThat(result, notNullValue());
    assertEquals(true,
        result.getReservationDetails().getRooms().stream().findAny().get().getAdultsMeal()
            .isEmpty());
  }

  @Test
  void getHotelInformation_WhenCalled_ThenMapperAndClientIsVerified() {
    var hotelInformationRequest = HotelInformationRequest.builder()
          .hotelId("hotelId")
          .build();
    var hotelInformationRequestDto = HotelInformationRequestDto.builder()
          .hotelId("hotelId")
          .build();
    var hotelInformationResponseDto = HotelInformationResponseDto.builder()
          .thumbnailImages(null)
          .build();
    var hotelInformationResponse = HotelInformationResponse.builder()
          .thumbnailImages(null)
          .build();

    when(contentRequestMapper.toDto(hotelInformationRequest))
          .thenReturn(hotelInformationRequestDto);
    when(contentRequestMapper.toModel(hotelInformationResponseDto))
          .thenReturn(hotelInformationResponse);
    when(contentClient.getHotelInformation(hotelInformationRequestDto))
          .thenReturn(hotelInformationResponseDto);

    var result = outService.getHotelInformation(hotelInformationRequest);

    assertEquals(hotelInformationResponse, result);
    verify(contentRequestMapper).toDto(hotelInformationRequest);
    verify(contentRequestMapper).toModel(hotelInformationResponseDto);
    verify(contentRequestMapper).toModel(hotelInformationResponseDto);
    verify(contentClient).getHotelInformation(hotelInformationRequestDto);
  }

  @Test
  void getReservationInformationAuth_WhenCalled_ThenMapperAndClientIsVerified() {
    var expectedToken = "Bearer token==";
    var expectedBasketReference = "br";
    var reservationDetailsDto = ReservationDetailsDto
          .builder()
          .reservationStatus("canceled")
          .build();
    var reservatoinByIdList = List.of(reservationDetailsDto);
    var reservationResponseDto = ReservationResponseDto.builder()
          .reservationByIdList(reservatoinByIdList)
          .build();
    var bookingRoomList = List.of(BookingRoom.builder().roomType("roomType").build());

    when(reservationClient.getReservationInformationAuth(expectedToken, expectedBasketReference))
          .thenReturn(reservationResponseDto);
    when(reservationResponseMapper.toModel(reservatoinByIdList))
          .thenReturn(bookingRoomList);

    var result = outService.getReservationInformationAuth(expectedToken, expectedBasketReference);

    assertEquals(bookingRoomList, result);
    verify(reservationClient).getReservationInformationAuth(expectedToken, expectedBasketReference);
    verify(reservationResponseMapper).toModel(reservatoinByIdList);
  }

  @Test
  void getStayDates_ReservationsFound() {
    // Arrange
    String bookingReference = "BR123";
    BasketDto basketDto = new BasketDto();
    basketDto.setHotelId("HOTEL1");
    var basketItem1 = new BasketItemDto();
    basketItem1.setSourceId("source1");
    var basketItem2 = new BasketItemDto();
    basketItem2.setSourceId("source2");
    basketDto.setItems(List.of(basketItem1, basketItem2));
    ReservationLightweightResponseDto reservationsResponse = new ReservationLightweightResponseDto();
    LightweightReservationByIdDto lightweightReservationByIdDto = LightweightReservationByIdDto.builder()
        .reservationId("res1")
        .checkInTime("11:00")
        .checkOutTime("14:00")
        .email("test@yopmail.com")
        .build();
    reservationsResponse.setReservationByIdList(List.of(lightweightReservationByIdDto));

    var packageDetails = new HashSet<>(List.of(new BookingPackagesDetails()));
    when(basketClient.sendGetBasketByReference(bookingReference)).thenReturn(ResponseEntity.ok(basketDto));
    when(ohipAdapterClient.getLightweightReservationsByIds("HOTEL1", Set.of("source1", "source2")))
        .thenReturn(reservationsResponse);
    when(reservationResponseMapper.toBookingPackagesModel(lightweightReservationByIdDto.getReservationPackageList()))
        .thenReturn(packageDetails);

    // Act
    List<UpcomingBookings> result = outService.getStayDates("token", bookingReference);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("11:00", result.get(0).checkInTime());
    assertEquals("14:00", result.get(0).checkOutTime());
    assertEquals("test@yopmail.com", result.get(0).email());
    assertEquals(1, result.get(0).extrasItems().size());
    assertSame(packageDetails, result.get(0).extrasItems());
  }

  @Test
  void getStayDates_BasketIsNull() {
    // Arrange
    String bookingReference = "BR123";
    when(basketClient.sendGetBasketByReference(bookingReference)).thenReturn(null);

    // Act
    List<UpcomingBookings> result = outService.getStayDates("token", bookingReference);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getStayDates_NoItemsInBasket() {
    // Arrange
    String bookingReference = "BR123";
    BasketDto basketDto = new BasketDto();
    basketDto.setItems(new ArrayList<>());
    when(basketClient.sendGetBasketByReference(bookingReference)).thenReturn(ResponseEntity.ok(basketDto));

    // Act
    List<UpcomingBookings> result = outService.getStayDates("token", bookingReference);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getStayDates_NoReservationIdsFound() {
    // Arrange
    String bookingReference = "BR123";
    BasketDto basketDto = new BasketDto();
    basketDto.setItems(List.of(new BasketItemDto()));
    when(basketClient.sendGetBasketByReference(bookingReference)).thenReturn(ResponseEntity.ok(basketDto));

    // Act
    List<UpcomingBookings> result = outService.getStayDates("token", bookingReference);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  private BookingPackagesDetails getUpdatedAdultMealsPackage(final BookingInfoResponse result){
    List<BookingRoom> rooms = result.getReservationDetails().getRooms();
    for(BookingRoom room : rooms) {
      var adultMealsPackage = room.getAdultsMeal()
          .stream()
          .filter(adultMeal -> adultMeal.getPackageCode().equalsIgnoreCase("BFADBF"))
          .findAny();
      if (adultMealsPackage.isPresent()) {
        return adultMealsPackage.get();
      }
    }
    return null;
  }

  private BookingPackagesDetails getUpdatedKidMealsPackage(final BookingInfoResponse result){
    List<BookingRoom> rooms = result.getReservationDetails().getRooms();
    for(BookingRoom room : rooms) {
      var kidMealsPackage = room.getKidsMeal()
          .stream()
          .filter(kidMeal -> kidMeal.getPackageCode().equalsIgnoreCase("BFCHDF"))
          .findAny();
      if (kidMealsPackage.isPresent()) {
        return kidMealsPackage.get();
      }
    }
    return null;
  }
  //END -- DNRQ-54871

  private BookingDetails emptyMealsBookingDetails() {

    return BookingDetails
        .builder()
        .bookingReference(BOOKING_REFERENCE)
        .basketReference(BASKET_REFERENCE)
        .rooms(List.of(BookingRoom
            .builder()
            .adultsMeal(Set.of())
            .kidsMeal(Set.of())
            .build()))
        .outstandingAmount(BookingPrice
            .builder()
            .build())
        .donationsPackage(packageDetails())
        .prepaidAmount(BookingPrice
            .builder()
            .build())
        .cancellationInfoResponse(CancellationInfoResponse
            .builder()
            .cancelable(false)
            .build())
        .dinnerAllowance(BookingPrice.builder()
            .amount(BigDecimal.ZERO)
            .currency("Dinner allowance")
            .build())
        .build();
  }

  private BookingInfoRequest emptyBookingInfoRequest() {
    return BookingInfoRequest.builder()
        .bookingReference("")
        .surname("")
        .arrival("")
        .build();
  }

  private BookingInfoRequest bookingInfoRequest() {
    return BookingInfoRequest.builder()
        .surname(SURNAME)
        .hotelId(HOTEL_ID)
        .arrival(ARRIVAL_DATE)
        .bookingReference(BOOKING_REFERENCE)
        .build();
  }

  private BookingInfoRequest bookingInfoRequestNotComplete() {

    return BookingInfoRequest.builder()
            .surname(SURNAME)
            .hotelId(HOTEL_ID)
            .arrival(ARRIVAL_DATE)
            .bookingReference("")
            .build();
  }

  private BookingChannel bookingChannelBB() {
    return BookingChannel.builder()
        .channel(CHANNEL_BB.getValue())
        .build();
  }

  private BookingChannel bookingChannelPI() {
    return BookingChannel.builder()
        .channel(CHANNEL_PI.getValue())
        .subchannel(SUBCHANNEL_PI.getValue())
        .build();
  }

  private ReservationAllowancesDto reservationAllowancesDto() {
    return ReservationAllowancesDto.builder()
        .bookingAllowances(List.of(reservationAllowanceDto()))
        .build();
  }

  private ReservationAllowanceDto reservationAllowanceDto() {
    return ReservationAllowanceDto.builder()
        .allowance("Dinner allowance")
        .budget(BigDecimal.ZERO)
        .build();
  }

  private ReservationChannelDto reservationChannelBBDto(){
    return ReservationChannelDto.builder()
        .channel(CHANNEL_BB.getValue())
        .subchannel(BOOKING_SUBCHANNEL)
        .build();
  }

  private ReservationChannelDto reservationChannelPIDto(){
    return ReservationChannelDto.builder()
        .channel(CHANNEL_PI.getValue())
        .subchannel(BOOKING_SUBCHANNEL)
        .build();
  }

  private ReservationRateInformationRequestDto reservationRateInformationBBRequest() {

    return ReservationRateInformationRequestDto.builder()
        .country("gb")
        .language("en")
        .hotelId(HOTEL_ID)
        .channel(CHANNEL_BB.getValue())
        .build();
  }

  private ReservationRateInformationRequestDto reservationRateInformationPIRequest() {

    return ReservationRateInformationRequestDto.builder()
        .country("gb")
        .language("en")
        .hotelId(HOTEL_ID)
        .channel(CHANNEL_PI.getValue())
        .build();
  }

  private ReservationResponseDto reservationResponse() {

    return ReservationResponseDto
        .builder()
        .basketReference(BASKET_REFERENCE)
        .reservationByIdList(List.of(ReservationDetailsDto
            .builder()
            .reservationStatus(BOOKING_STATUS)
            .onHold(true)
            .reservationGuestList(List.of(ReservationGuestDto
                .builder()
                .build()))
            .billing(ReservationBillingDto
                .builder()
                .build())
            .reservationCancelInfoResponse(ReservationCancelInfoResponseDto
                .builder()
                .build())
            .reservationPackageList(List.of(ReservationPackagesDetailsDto
                .builder()
                .build()))
            .roomStay(ReservationRoomDto
                .builder()
                .checkOutTime(RESERVATION_CHECKOUT_TIME)
                .departureDate(RESERVATION_DEPARTURE_DATE)
                .arrivalDate(RESERVATION_ARRIVAL_DATE)
                .build())
                .roomStay(ReservationRoomDto
                .builder()
                .checkOutTime(RESERVATION_CHECKOUT_TIME)
                .departureDate(RESERVATION_DEPARTURE_DATE)
                .arrivalDate(RESERVATION_ARRIVAL_DATE)
                .build())
            .build(),
                ReservationDetailsDto
                .builder()
                .roomStay(ReservationRoomDto
                .builder()
                .checkOutTime(RESERVATION_CHECKOUT_TIME)
                .departureDate(RESERVATION_DEPARTURE_DATE)
                .build())
                .roomStay(ReservationRoomDto
                .builder()
                .checkOutTime(RESERVATION_CHECKOUT_TIME)
                .departureDate(RESERVATION_DEPARTURE_DATE)
                .build())
            .build()))
        .build();
  }

  private ReservationResponseDto reservationResponseWithCityTax() {
    return ReservationResponseDto
        .builder()
        .basketReference(BASKET_REFERENCE)
        .reservationByIdList(List.of(ReservationDetailsDto
            .builder()
            .reservationStatus(BOOKING_STATUS)
            .onHold(true)
            .reservationGuestList(List.of(ReservationGuestDto.builder().build()))
            .billing(ReservationBillingDto.builder().build())
            .reservationCancelInfoResponse(ReservationCancelInfoResponseDto.builder().build())
            .reservationPackageList(List.of(ReservationPackagesDetailsDto.builder().build()))
            .roomStay(ReservationRoomDto
                .builder()
                .checkOutTime(RESERVATION_CHECKOUT_TIME)
                .departureDate(RESERVATION_DEPARTURE_DATE)
                .arrivalDate(RESERVATION_ARRIVAL_DATE)
                .ratesPerNight(List.of(
                    ReservationRatePerNightDto.builder().cityTaxPerNight(new BigDecimal("3.00")).build(),
                    ReservationRatePerNightDto.builder().cityTaxPerNight(new BigDecimal("3.00")).build()
                ))
                .build())
            .build()))
        .build();
  }

  private ReservationBasketOptionsResponseDto basketOptionsResponse() {

    return ReservationBasketOptionsResponseDto
        .builder()
        .paymentOption(PAYMENT_OPTION)
        .reference(BASKET_REFERENCE)
        .build();
  }

  private ReservationCancelInfoResponseDto cancelInfoResponse() {

    return ReservationCancelInfoResponseDto
        .builder()
        .isCancellable(false)
        .isAmendable(false)
        .build();
  }

  private ReservationCancelInfoResponseDto cancelInfoNotCancelAmendInfoResponse() {

    return ReservationCancelInfoResponseDto
            .builder()
            .isCancellable(false)
            .isAmendable(true)
            .build();
  }

  private ReservationCancelInfoResponseDto cancelInfoResponseCancelable() {

    return ReservationCancelInfoResponseDto
            .builder()
            .isCancellable(true)
            .isAmendable(true)
            .build();
  }

  private ReservationRateInformationResponseDto rateInformationResponse() {

    return ReservationRateInformationResponseDto
        .builder()
        .rateClassifications(List.of(ReservationRateClasificationsDto
            .builder()
            .rateClassification("FLEX")
            .rateNotes("Amend or cancel up to 1 pm.")
            .build()))
        .build();
  }

  private BookingDetails bookingDetails() {

    return BookingDetails
            .builder()
            .bookingReference(BOOKING_REFERENCE)
            .basketReference(BASKET_REFERENCE)
            .rooms(List.of(BookingRoom
                    .builder()
                    .adultsMeal(Set.of(packageDetails()))
                    .kidsMeal(Set.of(packageDetails()))
                    .extrasItems(Set.of(packageDetails()))
                    .build()))
            .outstandingAmount(BookingPrice
                    .builder()
                    .build())
            .donationsPackage(packageDetails())
            .prepaidAmount(BookingPrice
                    .builder()
                    .build())
            .cancellationInfoResponse(CancellationInfoResponse
                    .builder()
                    .cancelable(false)
                    .build())
            .dinnerAllowance(BookingPrice.builder()
                    .amount(BigDecimal.ZERO)
                    .currency("Dinner allowance")
                    .build())
            .build();
  }

  private BookingDetails bookingDetailsWithExtras() {

    return BookingDetails
            .builder()
            .bookingReference(BOOKING_REFERENCE)
            .basketReference(BASKET_REFERENCE)
            .rooms(List.of(BookingRoom
                    .builder()
                    .adultsMeal(Set.of(packageDetails()))
                    .kidsMeal(Set.of(packageDetails()))
                    .extrasItems(Set.of(extrasPackageDetails()))
                    .build()))
            .outstandingAmount(BookingPrice
                    .builder()
                    .build())
            .donationsPackage(packageDetails())
            .prepaidAmount(BookingPrice
                    .builder()
                    .build())
            .cancellationInfoResponse(CancellationInfoResponse
                    .builder()
                    .cancelable(false)
                    .build())
            .dinnerAllowance(BookingPrice.builder()
                    .amount(BigDecimal.ZERO)
                    .currency("Dinner allowance")
                    .build())
            .build();
  }

  private BookingPackagesDetails packageDetails(){
    return BookingPackagesDetails
        .builder()
        .totalPrice(BookingPrice.builder().build())
        .build();
  }

  private BookingPackagesDetails extrasPackageDetails(){
    return BookingPackagesDetails
            .builder()
            .packageCode(FI24HR)
            .totalPrice(BookingPrice.builder()
                    .amount(new BigDecimal(5))
                    .build())
            .build();
  }

  private BookingInfoResponse bookingInfoResponse() {

    return BookingInfoResponse
            .builder()
            .reservationDetails(bookingDetails())
            .checkInTime(LocalTime.now())
            .checkOutTime(LocalTime.now())
            .build();
  }

  private CancelBookingResponse cancelBookingResponse() {
    return CancelBookingResponse
        .builder()
        .bookingReference(BASKET_REFERENCE)
        .cancellationId(null)
        .build();
  }

  private BaseEmailRequest baseEmailRequest() {
    return BaseEmailRequest
        .builder()
        .bookingReference(BOOKING_REFERENCE)
        .hotelId(HOTEL_ID)
        .email(EMAIL)
        .build();
  }

  private BaseOperaEmailRequestDto baseOperaEmailRequest() {
    return BaseOperaEmailRequestDto
        .builder()
        .hotelId(HOTEL_ID)
        .email(EMAIL)
        .bookingReference(BOOKING_REFERENCE)
        .build();
  }

  private Jwt getJwt(){
    return new Jwt("authorization", Instant.now(), Instant.now().plusSeconds(60),
        Map.of("header1", "header2"), Map.of("Claim1", "Claim2"));
  }

  private Account getAccount() {
    return Account.builder()
        .bartId("187")
        .operaCompanyId("7765828")
        .companyId("5445")
        .accessLevel("SELF")
        .email("finalEmail")
        .customerId("5445")
        .employeeId("1")
        .build();
  }

  //START

  private ReservationResponseDto reservationResponse(boolean isMealsPackageRequired) {
    ReservationResponseDto reservationResponse = reservationResponse();
    if(isMealsPackageRequired) {
      reservationResponse.getReservationByIdList()
          .stream()
          .forEach(reservationDetails -> {
                List<ReservationPackagesDetailsDto> mealPackages =
                    List.of(
                        ReservationPackagesDetailsDto
                            .builder()
                            .packageCode("BFADBF")
                            .description("Premier Inn Breakfast Food")
                            .totalQuantity(1)
                            .unitPrice(BigDecimal.valueOf(20))
                            .computedPrice(BigDecimal.valueOf(30))
                            .build(),
                        ReservationPackagesDetailsDto
                            .builder()
                            .packageCode("BFCHDF")
                            .description("Free breakfast for kids")
                            .totalQuantity(1)
                            .unitPrice(BigDecimal.valueOf(10))
                            .computedPrice(BigDecimal.valueOf(30))
                            .build());
                reservationDetails.setReservationPackageList(mealPackages);
              });
    }
    return reservationResponse;
  }

  private BookingInfoResponse bookingInfoResponse(boolean withAemUpdates) {

    return BookingInfoResponse
        .builder()
        .reservationDetails(bookingDetails(withAemUpdates))
        .checkInTime(LocalTime.now())
        .checkOutTime(LocalTime.now())
        .build();
  }

  private BookingInfoResponse emptyMealsBookingInfoResponse() {

    return BookingInfoResponse
        .builder()
        .reservationDetails(emptyMealsBookingDetails())
        .checkInTime(LocalTime.now())
        .checkOutTime(LocalTime.now())
        .build();
  }

  private BookingDetails bookingDetails(final boolean withAemUpdates) {
    BookingDetails bookingDetails = bookingDetails();
    if (withAemUpdates) {
      bookingDetails.getRooms()
          .stream()
          .forEach(room -> {
            Set<BookingPackagesDetails> adultMeals = Set.of(
                BookingPackagesDetails.builder()
                    .packageCode("BFADBF")
                    .description("Premier Inn Breakfast AEM")
                    .noSelections(1)
                    .totalPrice(BookingPrice
                        .builder()
                        .amount(BigDecimal.valueOf(20))
                        .currency("GBP")
                        .build())
                    .build());
            room.setAdultsMeal(adultMeals);

            Set<BookingPackagesDetails> kidsMeals = Set.of(
                BookingPackagesDetails.builder()
                    .packageCode("BFCHDF")
                    .description("Free breakfast for kids AEM")
                    .noSelections(1)
                    .totalPrice(BookingPrice
                        .builder()
                        .amount(BigDecimal.valueOf(10))
                        .currency("GBP")
                        .build())
                    .build());
            room.setKidsMeal(kidsMeals);
          });
    } else {
      bookingDetails.getRooms()
          .stream()
          .forEach(room -> {
            Set<BookingPackagesDetails> adultMeals = Set.of(
                BookingPackagesDetails.builder()
                    .packageCode("BFADBF")
                    .description("Premier Inn Breakfast Food")
                    .noSelections(1)
                    .totalPrice(BookingPrice
                        .builder()
                        .amount(BigDecimal.valueOf(20))
                        .currency("GBP")
                        .build())
                    .build());
            room.setAdultsMeal(adultMeals);

            Set<BookingPackagesDetails> kidsMeals = Set.of(
                BookingPackagesDetails.builder()
                    .packageCode("BFCHDF")
                    .description("Free breakfast for kids")
                    .noSelections(1)
                    .totalPrice(BookingPrice
                        .builder()
                        .amount(BigDecimal.valueOf(10))
                        .currency("GBP")
                        .build())
                    .build());
            room.setKidsMeal(kidsMeals);
          });
    }
    return bookingDetails;
  }

  private Set<BookingPackagesDetails> updatedAdultsMealPackages() {
    return Set.of (
        BookingPackagesDetails.builder()
            .packageCode("BFADBF")
            .description("Premier Inn Breakfast AEM")
            .noSelections(1)
            .totalPrice(BookingPrice
                .builder()
                .amount(BigDecimal.valueOf(20))
                .currency("GBP")
                .build())
            .build());
  }

  private Set<BookingPackagesDetails> updatedKidsMealPackages() {
    return Set.of (
        BookingPackagesDetails.builder()
            .packageCode("BFCHDF")
            .description("Free breakfast for kids AEM")
            .noSelections(1)
            .totalPrice(BookingPrice
                .builder()
                .amount(BigDecimal.valueOf(10))
                .currency("GBP")
                .build())
            .build());
  }

  private MealsRequestDto aemMealsInformationPIRequest() {
    return MealsRequestDto.builder()
        .country("gb")
        .language("en")
        .hotelId(HOTEL_ID)
        .build();
  }

  private MealsInfoResponseDto aemMealsInformationResponse() {
    return MealsInfoResponseDto
        .builder()
        .upsellItems(List.of(
            UpsellItemsDto.builder()
                .code("BFADBF")
                .name("Premier Inn Breakfast AEM")
                .build(),
            UpsellItemsDto.builder()
                .code("BFCHDF")
                .name("Free breakfast for kids AEM")
                .build())).build();
  }

  private PackageGroupOhipResponseDto packageGroupsResponse() {
    return
        PackageGroupOhipResponseDto.builder()
            .packagesGroup(List.of(PackageGroupsDto.builder()
                .packageGroup("MDP")
                .packageGroupDescription("Meal Deal Package")
                .packageCodes(List.of(
                    PackageCodesDto.builder().packageCode("MDBEVA").packageDescription("Meal Deal Dinner").build(),
                    PackageCodesDto.builder().packageCode("MD2DIN").packageDescription("Meal Deal Dinner Beverage").build(),
                    PackageCodesDto.builder().packageCode("MDBFST").packageDescription("Meal Deal Breakfast Food").build()
                ))
                .build()))

            .build();
  }

  //END
}
