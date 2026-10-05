package uk.co.whitbread.booking.infrastructure.rest.client.reservation;

import static uk.co.whitbread.booking.domain.model.channel.BookingChannelValues.CHANNEL_BB;
import static uk.co.whitbread.booking.domain.model.channel.BookingChannelValues.CHANNEL_PI;
import static uk.co.whitbread.booking.domain.model.exceptions.ErrorCode.DIGITAL_CANCEL_BOOKING_INVALID_AUTHORIZATION_EXCEPTION;
import static uk.co.whitbread.booking.domain.model.exceptions.ErrorCode.DIGITAL_GET_ACCESS_LEVEL_EXCEPTION;
import static uk.co.whitbread.booking.domain.model.exceptions.ErrorCode.DIGITAL_GET_OPERA_BOOKING_EXCEPTION;
import static uk.co.whitbread.booking.domain.model.exceptions.ErrorCode.DIGITAL_GET_TOKEN_EXCEPTION;
import static uk.co.whitbread.booking.infrastructure.rest.client.reservation.utils.ReservationUtils.isPastBooking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.BaseEmailRequest;
import uk.co.whitbread.booking.domain.model.exceptions.AuthorizationException;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.in.HotelInformationRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.BookingPackagesDetails;
import uk.co.whitbread.booking.domain.model.information.out.BookingRoom;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.information.out.HotelInformationResponse;
import uk.co.whitbread.booking.domain.model.information.out.UpcomingBookings;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;
import uk.co.whitbread.booking.domain.ports.secondary.ReservationOutPort;
import uk.co.whitbread.booking.infrastructure.exceptions.BadRequestException;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service.CdhAdapterClient;
import uk.co.whitbread.booking.infrastructure.rest.client.content.service.ContentClient;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.in.PackageGroupRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageGroupOhipResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.PackageGroupsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice.InvoiceDownloadService;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ContentRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ReservationRequestMapper;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.mapper.ReservationResponseConverter;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationChannelDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationAllowancesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationBasketOptionsResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationCancelInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationPackagesDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRatePerNightDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationRoomDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Slf4j
@RequiredArgsConstructor
public class ReservationOutPortImpl implements ReservationOutPort {

  private static final String NOT_AUTHORIZED = "Not authorized";
  private static final String GUEST = "STAYER";

  private static final String FI24HR = "FI24HR";
  private final ReservationClient reservationClient;
  private final BasketClient basketClient;
  private final ContentClient contentClient;
  private final ReservationResponseConverter reservationResponseMapper;
  private final ReservationRequestMapper reservationRequestMapper;
  private final ContentRequestMapper contentRequestMapper;
  private final AuthenticatedUserService authenticatedUserService;
  private final OhipAdapterClient ohipAdapterClient;
  private final CdhAdapterClient cdhAdapterClient;
  private final InvoiceDownloadService invoiceDownloadService;

  @Override
  public BookingInfoResponse getOperaBookingInformation(BookingInfoRequest bookingInfoRequest,
      BookingChannel bookingChannel) {
    var reservationChannel = reservationRequestMapper.toDto(bookingChannel);
    ReservationResponseDto reservationInfoResponse;
    ReservationBasketOptionsResponseDto basketOptions;

    if (CHANNEL_PI.getValue().equals(reservationChannel.getChannel())) {
      if (!bookingInfoRequest.getBookingReference().isEmpty() && !bookingInfoRequest.getSurname().isEmpty()
          && !bookingInfoRequest.getArrival().isEmpty()) {
        basketOptions =
                basketClient.getBasketByBookingReference(bookingInfoRequest.getBookingReference());
        reservationInfoResponse = reservationClient.getReservationInformation(basketOptions.getReference());

      } else {
        var message = String.format(
            "Booking info request does not contain required fields for searching: %s. "
                + "Please supply required fields for search.", bookingInfoRequest);
        var exception = new BadRequestException(DIGITAL_GET_OPERA_BOOKING_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    } else {
      reservationInfoResponse =
          reservationClient.getReservationInformationAuth(getToken(),
              bookingInfoRequest.getBookingReference());
      basketOptions =
              basketClient.getBasketOptions(reservationInfoResponse.getBasketReference());
    }
    var rateInformation =
            contentClient.getRateInformation(reservationRequestMapper.toModel(bookingInfoRequest,
            reservationChannel.getChannel()));

    var cancelResponse =
        retrieveCancelResponse(bookingInfoRequest, reservationChannel, reservationInfoResponse);
    var allowances = getBookingAllowances(reservationInfoResponse.getBasketReference());

    if (CHANNEL_BB.getValue().equals(reservationChannel.getChannel()) && cancelResponse.getIsCancellable() != null
            && Boolean.TRUE.equals(cancelResponse.getIsCancellable()
            && reservationInfoResponse.getReservationByIdList().size() > 1) && getAccessLevel().equals(GUEST)) {
      cancelResponse.setIsCancellable(false);
    }
    
    var cityTaxTotal = calculateCityTaxTotal(reservationInfoResponse);
    var ohipPackageGroupsResponse = getOhipPackageGroupsResponse(bookingInfoRequest,
        reservationInfoResponse);
    var bookingDetails = reservationResponseMapper.toModel(
        reservationInfoResponse, basketOptions,
        rateInformation, cancelResponse,
        bookingInfoRequest.getBookingReference(), allowances,
        ohipPackageGroupsResponse, cityTaxTotal
    );
    //DNRQ-54871 - Update Premier Inn Breakfast Description from AEM
    updateMealPackages(bookingDetails, bookingInfoRequest);
    setCurrencyCodeAndTotalPriceForExtrasItems(bookingDetails, reservationInfoResponse);
    return reservationResponseMapper.toModel(reservationInfoResponse, bookingDetails);
  }

  private List<PackageGroupsDto> getOhipPackageGroupsResponse(
      BookingInfoRequest bookingInfoRequest,
      ReservationResponseDto reservationInfoResponse) {

    PackageGroupRequestDto packageGroupsRequestDto = new PackageGroupRequestDto();

    List<ReservationDetailsDto> reservations = Optional.ofNullable(reservationInfoResponse)
        .map(ReservationResponseDto::getReservationByIdList)
        .orElse(Collections.emptyList());

    Set<String> packageGroups = reservations.stream()
        .filter(Objects::nonNull)
        .flatMap(reservation -> Optional.ofNullable(reservation.getReservationPackageList())
            .orElse(Collections.emptyList()).stream())
        .map(ReservationPackagesDetailsDto::getPackageGroup)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    packageGroupsRequestDto.setPackageGroupList(packageGroups);
    packageGroupsRequestDto.setHotelId(bookingInfoRequest.getHotelId());

    return Optional.ofNullable(ohipAdapterClient.getPackageGroups(packageGroupsRequestDto))
        .map(PackageGroupOhipResponseDto::getPackagesGroup)
        .orElse(Collections.emptyList());
  }


  private void setCurrencyCodeAndTotalPriceForExtrasItems(BookingDetails bookingDetails,
                                                          ReservationResponseDto reservationInfoResponse) {
    if (bookingDetails != null) {
      bookingDetails.getRooms().forEach(room -> {
        if (Objects.nonNull(room.getExtrasItems())) {
          room.getExtrasItems().forEach(extras -> {
            extras.getTotalPrice().setCurrency(reservationInfoResponse.getCurrencyCode());
            if (FI24HR.equals(extras.getPackageCode())) {
              calculateTotalPriceForWifi(reservationInfoResponse, extras);
            }
          });
        }
      });
    }
  }

  private void calculateTotalPriceForWifi(ReservationResponseDto reservationInfoResponse,
                                          BookingPackagesDetails extras) {
    var arrival = LocalDate.parse(reservationInfoResponse.getReservationByIdList().get(0)
            .getRoomStay().getArrivalDate());
    var departure =  LocalDate.parse(reservationInfoResponse.getReservationByIdList().get(0)
            .getRoomStay().getDepartureDate());
    var noOfNights = BigDecimal.valueOf(ChronoUnit.DAYS.between(arrival, departure));
    var totalPrice = extras.getTotalPrice().getAmount().multiply(noOfNights);
    extras.getTotalPrice().setAmount(totalPrice);
  }

  private BigDecimal calculateCityTaxTotal(ReservationResponseDto reservationInfoResponse) {
    return Optional.ofNullable(reservationInfoResponse)
        .map(ReservationResponseDto::getReservationByIdList)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .map(reservation -> Optional.ofNullable(reservation.getRoomStay())
            .map(ReservationRoomDto::getRatesPerNight)
            .orElse(Collections.emptyList())
            .stream()
            .filter(Objects::nonNull)
            .map(ReservationRatePerNightDto::getCityTaxPerNight)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void updateMealPackages(BookingDetails bookingDetails,
      BookingInfoRequest bookingInfoRequest) {
    if (bookingDetails != null) {
      boolean updateMealDescription =
          bookingDetails.getRooms()
              .stream()
              .anyMatch(room -> (room.getAdultsMeal().size() > 0 || room.getKidsMeal().size() > 0));
      if (updateMealDescription) {
        var aemMealsInformation =
            contentClient.getMealsInformation(contentRequestMapper.toModel(bookingInfoRequest));
        bookingDetails.getRooms()
            .stream()
            .forEach(room -> {
              room.setAdultsMeal(
                  reservationResponseMapper
                      .getUpdatedMealPackages(room.getAdultsMeal(), aemMealsInformation));
              room.setKidsMeal(
                  reservationResponseMapper
                      .getUpdatedMealPackages(room.getKidsMeal(), aemMealsInformation));
            });
      }
    }
  }

  @Override
  public CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest) {
    log.info("Cancelbookingrequest: {}", cancelBookingRequest);

    var cancelReservationRequestDto = reservationRequestMapper.toDto(cancelBookingRequest);
    JwtAuthenticationToken authenticatedUser;
    try {
      authenticatedUser = authenticatedUserService.getAuthenticatedUser();
    } catch (ClassCastException e) {
      var token = cancelBookingRequest.getToken();
      if (StringUtils.isNotEmpty(token)) {
        var cancelReservationResponseDto = reservationClient.cancelReservation(null, cancelReservationRequestDto);
        return reservationResponseMapper.toModel(cancelReservationResponseDto);
      }
      var exception = new AuthorizationException(DIGITAL_CANCEL_BOOKING_INVALID_AUTHORIZATION_EXCEPTION,
          NOT_AUTHORIZED);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    var cancelReservationResponseDto = reservationClient.cancelReservation("Bearer "
        + authenticatedUser.getToken().getTokenValue(), cancelReservationRequestDto);
    return reservationResponseMapper.toModel(cancelReservationResponseDto);

  }

  @Override
  public void sendBookingConfirmationOrInvoiceEmail(
      BaseEmailRequest baseEmailRequest) {
    var emailRequest = reservationRequestMapper.toDto(baseEmailRequest);
    basketClient.sendBookingConfirmationOrInvoiceEmail(emailRequest);
  }

  @Override
  public void findBooking(BookingInfoRequest bookingInfoRequest, BookingChannel bookingChannel, boolean isPastBooking) {
    reservationClient.findBooking(
          reservationRequestMapper.toDto(bookingInfoRequest, bookingChannel, isPastBooking));
  }

  @Override
  public List<BookingRoom> getReservationInformationAuth(String token, String basketReference) {
    ReservationResponseDto reservationInformationAuth = reservationClient.getReservationInformationAuth(
          token, basketReference);

    return reservationResponseMapper.toModel(
          reservationInformationAuth.getReservationByIdList());
  }

  @Override
  public HotelInformationResponse getHotelInformation(HotelInformationRequest request) {
    var result = contentClient.getHotelInformation(contentRequestMapper.toDto(request));

    return contentRequestMapper.toModel(result);
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()",
      cacheManager = "cacheManager1Day",
      value = "IBUpcomingBookings",
      key = "#bookingReference")
  public List<UpcomingBookings> getStayDates(String token, String bookingReference) {
    var basket = basketClient.sendGetBasketByReference(bookingReference);
    if (basket == null) {
      log.warn("No basket found for booking reference: {}", bookingReference);
      return new ArrayList<>();
    }
    if (basket.getBody().getItems() == null || basket.getBody().getItems().isEmpty()) {
      log.info("No items found in basket for booking reference: {}", bookingReference);
      return new ArrayList<>();
    }
    var reservationIds = basket.getBody().getItems().stream()
        .map(BasketItemDto::getSourceId).collect(Collectors.toSet());
    if (reservationIds.isEmpty()) {
      log.info("No reservation IDs found in basket for booking reference: {}", bookingReference);
      return new ArrayList<>();
    }

    var reservationsResponse = ohipAdapterClient.getLightweightReservationsByIds(
        basket.getBody().getHotelId(), reservationIds);

    if (reservationsResponse == null || reservationsResponse.getReservationByIdList() == null) {
      log.warn("No reservations found for booking reference: {}", bookingReference);
      return new ArrayList<>();
    }

    return reservationsResponse.getReservationByIdList().stream()
        .map(booking -> {
          var mappedPackages = reservationResponseMapper.toBookingPackagesModel(booking.getReservationPackageList());
          return new UpcomingBookings(booking.getCheckInTime(),
              booking.getCheckOutTime(),
              mappedPackages,
              booking.getEmail());
        })
        // for redis deserialization compatibility we cannot use toList()
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public ReservationCancelInfoResponseDto retrieveCancelResponse(
      BookingInfoRequest bookingInfoRequest,
      ReservationChannelDto reservationChannel,
      ReservationResponseDto response) {

    var authToken = authenticatedUserService.isUserAuthenticated() ? getToken() : null;
    if (!isPastBooking(response)) {
      return reservationClient.getCancelReservationInformation(authToken,
          reservationRequestMapper.toDto(bookingInfoRequest, reservationChannel, response.getBasketReference()));
    }

    return ReservationCancelInfoResponseDto.builder()
        .isCancellable(false)
        .isAmendable(false)
        .build();
  }

  private ReservationAllowancesDto getBookingAllowances(String basketReference) {
    return reservationClient.getDinnerAllowances(basketReference);
  }

  private String getToken() {
    if (!authenticatedUserService.isUserAuthenticated()) {
      var exception = new AuthorizationException(DIGITAL_GET_TOKEN_EXCEPTION,
          NOT_AUTHORIZED);
      ExceptionLogger.log(log, exception);
      throw exception;

    }
    return "Bearer " + authenticatedUserService.getAuthenticatedUser().getToken().getTokenValue();
  }

  public String getAccessLevel() {
    if (!authenticatedUserService.isUserAuthenticated()) {
      var exception = new AuthorizationException(DIGITAL_GET_ACCESS_LEVEL_EXCEPTION,
          NOT_AUTHORIZED);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return authenticatedUserService.getAuthenticatedUser().getAccount().getAccessLevel();
  }

  @Override
  public InvoiceDownloadResponse downloadBookingInvoices(DownloadBookingInvoicesRequest request) {
    return invoiceDownloadService.generateInvoices(request);
  }

}
