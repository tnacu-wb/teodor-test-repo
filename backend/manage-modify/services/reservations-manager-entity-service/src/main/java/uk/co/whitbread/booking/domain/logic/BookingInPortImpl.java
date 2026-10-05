package uk.co.whitbread.booking.domain.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.email.in.ResendConfirmationEmailRequest;
import uk.co.whitbread.booking.domain.model.email.in.ResendInvoiceEmailRequest;
import uk.co.whitbread.booking.domain.model.exceptions.AuthorizationException;
import uk.co.whitbread.booking.domain.model.exceptions.BookingValidationException;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.domain.model.feature.FeatureFlag;
import uk.co.whitbread.booking.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.booking.domain.model.history.in.BookingRequest;
import uk.co.whitbread.booking.domain.model.history.in.BookingStatus;
import uk.co.whitbread.booking.domain.model.history.in.FilterTypes;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.in.HotelInformationRequest;
import uk.co.whitbread.booking.domain.model.information.out.BookingInfoResponse;
import uk.co.whitbread.booking.domain.model.information.out.BookingPackagesDetails;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.information.out.UpcomingBookings;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;
import uk.co.whitbread.booking.domain.model.migration.out.PmsSource;
import uk.co.whitbread.booking.domain.model.upcoming.in.UpcomingBookingRequest;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsCdhResponse;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsResponse;
import uk.co.whitbread.booking.domain.ports.primary.BookingInPort;
import uk.co.whitbread.booking.domain.ports.secondary.BookingOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.ReservationOutPort;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.cdh.model.bookings.UpcomingBooking;


@Slf4j
@RequiredArgsConstructor
public class BookingInPortImpl implements BookingInPort {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String EARLY_KEYWORD = "early";
  private static final String LATE_KEYWORD = "late";
  private static final String PACKAGE_CODE_2PM = "2";
  private static final String PI_BOOKING_CHANNEL = "PI";
  private static final String BB_BOOKING_CHANNEL = "BB";
  private static final LocalTime EARLY_CHECK_IN_TIME = LocalTime.of(11, 0);
  private static final LocalTime LATE_CHECK_OUT_2PM_TIME = LocalTime.of(14, 0);
  private static final LocalTime LATE_CHECK_OUT_4PM_TIME = LocalTime.of(16, 0);
  private static final LocalTime STANDARD_CHECK_IN_TIME = LocalTime.of(15, 0);
  private static final LocalTime STANDARD_CHECK_OUT_TIME = LocalTime.of(12, 0);
  private static final String INVALID_OR_MISSING_CLAIMS = "Invalid or missing claims";
  private final BookingOutPort bookingOutPort;
  private final ReservationOutPort reservationOutPort;
  private final Integer oldBookingThreshold;
  private final CheckInOnlineLogic checkInOnlineLogic;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final CdhOutPort cdhOutPort;
  private final AuthenticatedUserService authenticatedUserService;
  private final DigitalKeyFeature digitalKeyFeature;

  @Override
  public BookingResponse getBookings(String authorization, BookingRequest bookingRequest,
                                     BookingChannel bookingChannel) {
    BookingResponse bookingResponse = bookingOutPort
        .getBookings(authorization, bookingRequest, bookingChannel);

    if (bookingResponse.getBookings().isEmpty()) {
      return bookingResponse;
    }

    bookingResponse.getBookings().forEach(booking ->
          booking.setSourceSystem(PmsSource.OPERA.toString()));

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getPiBbMobileCheckInOnline())) {
      var eligibleBookingsForCiol = checkInOnlineLogic.getEligibleBookingsForCiol(bookingResponse);
      checkInOnlineLogic.setCiolDataEligibleBookings(bookingResponse, eligibleBookingsForCiol);

      //PIBA card filtering
      var checkInOnLineBookings = bookingResponse.getBookings().stream()
          .filter(Booking::isCheckInOnlineAvailable).toList();
      List<Booking> filteredByPibaCp = checkInOnlineLogic.filterByPibaCard(checkInOnLineBookings,
          bookingResponse.getBookings(),
          bookingChannel);
      bookingResponse.setBookings(filteredByPibaCp);
    }

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMobileDigitalKey())) {
      bookingResponse.getBookings().forEach(booking -> {
        var digitalKeyEnabled = digitalKeyFeature.isDigitalKeyAvailable(booking);
        log.info("Digital key flag: {} - for booking reference: {}", digitalKeyEnabled, booking.getBookingReference());
        digitalKeyFeature.setDigitalKeyFlag(booking, digitalKeyEnabled);
      });
    }

    return bookingResponse;
  }

  @Override
  public BookingInfoResponse getBookingInformation(BookingInfoRequest bookingInfoRequest,
                                                   BookingChannel bookingChannel) {
    findAndRecreateBasketIfOldBooking(bookingInfoRequest, bookingChannel);

    var bookingInfo = reservationOutPort.getOperaBookingInformation(bookingInfoRequest,
        bookingChannel);
    bookingInfo.getReservationDetails().setSourceSystem(PmsSource.OPERA);

    return bookingInfo;
  }

  private void findAndRecreateBasketIfOldBooking(BookingInfoRequest bookingInfoRequest, BookingChannel bookingChannel) {
    LocalDate arrivalDate = LocalDate.parse(bookingInfoRequest.getArrival(), DATE_FORMATTER);
    if (arrivalDate.isBefore(LocalDate.now().minusMonths(oldBookingThreshold))) {
      reservationOutPort.findBooking(bookingInfoRequest, bookingChannel, true);
    }
  }

  @Override
  public CancelBookingResponse cancelBooking(CancelBookingRequest cancelBookingRequest) {
    return reservationOutPort.cancelBooking(cancelBookingRequest);
  }

  @Override
  public void sendBookingConfirmationEmail(ResendConfirmationEmailRequest request) {
    reservationOutPort.sendBookingConfirmationOrInvoiceEmail(request);
  }

  @Override
  public void resendBookingInvoiceEmail(ResendInvoiceEmailRequest request) {
    reservationOutPort.sendBookingConfirmationOrInvoiceEmail(request);
  }

  @Override
  public UpcomingBookingsResponse getUpcomingBookings(UpcomingBookingRequest request,
        String authorization) {
    var authenticatedUser = authenticatedUserService.getAuthenticatedUser().getAccount();
    validateTokenClaims(authenticatedUser);

    var upcomingBookings = cdhOutPort.getUpcomingBookings(authenticatedUser.getCompanyId(),
          authenticatedUser.getEmployeeId(), authenticatedUser.getEmail());
    var result = UpcomingBookingsResponse.builder()
          .stays(upcomingBookings.getStays())
          .bookings(upcomingBookings.getBookings())
          .build();
    var upcomingBooking = getUpcomingBooking(upcomingBookings);
    if (upcomingBooking.isPresent()) {
      setBookingInfoToUpcomingBookings(result, upcomingBooking.get());
      setArrivalAndDepartureHoursToUpcomingBookings(result, authorization, authenticatedUser.getEmail());
      setAemRelatedInfoToUpcomingBookings(result, request, upcomingBooking.get());
    }

    return result;
  }

  private static void validateTokenClaims(Account authenticatedUser) {
    if (authenticatedUser.getEmployeeId() == null || authenticatedUser.getCompanyId() == null
          || authenticatedUser.getEmail() == null || authenticatedUser.getEmployeeId().isBlank()
          || authenticatedUser.getCompanyId().isBlank() || authenticatedUser.getEmail().isBlank()) {
      var exception = new AuthorizationException(ErrorCode.DIGITAL_INVALID_AUTHORIZATION_TOKEN_EXCEPTION,
            INVALID_OR_MISSING_CLAIMS);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private Optional<UpcomingBooking> getUpcomingBooking(UpcomingBookingsCdhResponse upcomingBookings) {
    var upcomingBookingList = Optional.ofNullable(upcomingBookings.getUpcomingBooking());
    if (upcomingBookingList.isEmpty() || upcomingBookingList.get().isEmpty()) {
      return Optional.empty();
    }
    return upcomingBookingList
          .get()
          .stream()
          .min(Comparator.comparing(UpcomingBooking::getArrivalDate));
  }

  private void setBookingInfoToUpcomingBookings(UpcomingBookingsResponse result,
        UpcomingBooking upcomingBooking) {
    result
          .setHotelName(upcomingBooking.getHotelName())
          .setBookingReference(upcomingBooking.getBookingReference())
          .setArrivalDate(upcomingBooking.getArrivalDate().toLocalDate())
          .setDepartureDate(upcomingBooking.getDepartureDate().toLocalDate());
  }

  private void setArrivalAndDepartureHoursToUpcomingBookings(UpcomingBookingsResponse result,
        String authorization, String email) {

    if (result.getBookingReference() == null || result.getBookingReference().isEmpty()) {
      return;
    }

    var bookings = reservationOutPort.getStayDates(authorization, result.getBookingReference());
    var packages = new ArrayList<BookingPackagesDetails>();

    for (var booking : bookings) {
      if (booking.email() != null && booking.email().equalsIgnoreCase(email)) {
        addBookingInfoToResult(result, packages, booking);
      }
      addDefaultArrivalAndDepartureTimes(result);
    }

    packages.stream()
          .filter(p -> p.getDescription().toLowerCase().contains(EARLY_KEYWORD))
          .findAny()
          .ifPresent(p -> result.setArrivalTime(EARLY_CHECK_IN_TIME));

    packages.stream()
          .filter(p -> p.getDescription().toLowerCase().contains(LATE_KEYWORD))
          .findAny()
          .ifPresent(p -> result.setDepartureTime(p.getPackageCode().contains(PACKAGE_CODE_2PM)
                ? LATE_CHECK_OUT_2PM_TIME : LATE_CHECK_OUT_4PM_TIME));
  }

  private void addDefaultArrivalAndDepartureTimes(UpcomingBookingsResponse result) {
    if (result.getArrivalTime() == null) {
      result.setArrivalTime(STANDARD_CHECK_IN_TIME);
    }
    if (result.getDepartureTime() == null) {
      result.setDepartureTime(STANDARD_CHECK_OUT_TIME);
    }
  }

  private void addBookingInfoToResult(UpcomingBookingsResponse result, ArrayList<BookingPackagesDetails> packages,
                                      UpcomingBookings booking) {
    result.setArrivalTime(booking.checkInTime() != null ? LocalTime.parse(booking.checkInTime()) : null);
    result.setDepartureTime(booking.checkOutTime() != null ? LocalTime.parse(booking.checkOutTime()) : null);

    if (Objects.nonNull(booking.extrasItems()) && !booking.extrasItems().isEmpty()) {
      packages.addAll(booking.extrasItems());
    }
  }

  private void setAemRelatedInfoToUpcomingBookings(UpcomingBookingsResponse result, UpcomingBookingRequest request,
        UpcomingBooking upcomingBooking) {

    var hotelInformation = reservationOutPort.getHotelInformation(HotelInformationRequest.builder()
          .hotelId(upcomingBooking.getHotelCode())
          .country(request.getCountry())
          .language(request.getLanguage())
          .channel(request.getChannel())
          .subChannel(request.getSubchannel())
          .build());

    if (hotelInformation != null) {
      result
            .setGalleryImages(hotelInformation.getGalleryImages())
            .setThumbnailImages(hotelInformation.getThumbnailImages())
            .setTopSectionImages(hotelInformation.getTopSectionImages())
            .setBrand(hotelInformation.getBrand())
            .setLinks(hotelInformation.getLinks());
    } else {
      log.error("Empty response received from content-entity-service when trying to retrieve hotel "
            + "info for hotel={}, country={}, language={}, channel={}, subchannel={}.",
            upcomingBooking.getHotelCode(), request.getCountry(), request.getLanguage(),
            request.getChannel(), request.getSubchannel());
    }
  }

  @Override
  public InvoiceDownloadResponse downloadBookingInvoices(String authorization,
      DownloadBookingInvoicesRequest request) {
    String channel = request.channel();
    boolean isPiChannel = PI_BOOKING_CHANNEL.equalsIgnoreCase(channel);
    boolean isBbChannel = BB_BOOKING_CHANNEL.equalsIgnoreCase(channel);

    if (!isPiChannel && !isBbChannel) {
      throw new BookingValidationException(
          ErrorCode.BOOKING_INVOICE_UNSUPPORTED_CHANNEL,
          "Invoice download is only supported for PI and BB channels. Received: " + channel);
    }

    String bookingRef = request.bookingRef().get(0);
    BookingRequest bookingRequest = BookingRequest.builder()
        .typeOfBooking(BookingStatus.PAST)
        .filterType(FilterTypes.CONFIRM_NUMBER)
        .filterValue(bookingRef)
        .business(isBbChannel)
        .build();

    BookingChannel bookingChannel = BookingChannel.builder()
        .channel(channel)
        .language(String.valueOf(request.lang()))
        .build();

    var bookings = getBookings(authorization, bookingRequest, bookingChannel);

    if (bookings.getBookings().isEmpty()) {
      throw new BookingValidationException(
          ErrorCode.BOOKING_INVOICE_NOT_FOUND,
          "No booking found for booking reference: " + bookingRef);
    }

    return reservationOutPort.downloadBookingInvoices(request);
  }

}
