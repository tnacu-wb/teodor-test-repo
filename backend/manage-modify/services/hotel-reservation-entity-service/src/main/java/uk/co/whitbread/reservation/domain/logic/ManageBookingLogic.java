package uk.co.whitbread.reservation.domain.logic;

import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.FRONT_DESK_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.isOtaBooking;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.PI_BOOKING_CHANNEL;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.logic.utils.PaymentUtils;
import uk.co.whitbread.reservation.domain.logic.utils.ReservationUtils;
import uk.co.whitbread.reservation.domain.logic.utils.TokenUtils;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.BusinessAllowance;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.BookingSearch;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.Config;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.DashboardRedirect;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleRequestDetails;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.FindBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.RulesOutPort;
import uk.co.whitbread.reservation.domain.properties.ThirdpartyBookingProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions.NoHeaderDataException;

@Slf4j
@AllArgsConstructor
public class ManageBookingLogic {
  public static final String THIRD_PARTY_RESERVATION_IS_NOT_ALLOWED =
      "Third-party reservation is not allowed for this channel, or the third-party provider is excluded.";
  private static final String ID_CONTEXT = "3rd Party";
  public static final String OPERA = "Opera";
  public static final String WB_DIGITAL = "WB_DIGITAL";
  public static final String SOURCE_35 = "35";
  public static final String SOURCE_38 = "38";
  public static final String SOURCE_43 = "43";
  public static final String SOURCE_01 = "01";
  public static final String SOURCE_31 = "31";

  private final BasketOutPort basketOutPort;
  private final RulesOutPort rulesOutPort;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final ContentOutPort contentOutPort;
  private final ThirdpartyBookingProperties otaBookingProperties;

  public BasketResponse createBasketForOperaUiCreatedReservations(String hotelId,
      String confirmationNumber,
      ReservationByBasketRefResponse rsvDetails,
      boolean isOta) {
    var basketStatus = CreateBasketRequestDto.BasketStatusEnum.COMPLETED;
    if (BasketDto.StatusEnum.CANCELLED.name().equals(rsvDetails.getReservationByIdList().get(0)
            .getReservationStatus())) {
      basketStatus = CreateBasketRequestDto.BasketStatusEnum.CANCELLED;
    }
    var basket = basketOutPort.createBasket(hotelId, null, basketStatus, confirmationNumber,
            PaymentUtils.getRsvPaymentOption(rsvDetails), null,
            getChannel(ReservationUtils.getReservationSourceCode(rsvDetails)), null);
    basket = basketOutPort.addReservationsToBasket(basket.getReference(),
            basket.getETag().replaceAll("[\"]", " ").trim(),
            (ReservationUtils.toOhipReservation(rsvDetails)), true, null);

    var depositFoliosDetails = rsvDetails.getReservationByIdList().get(0).getDepositFoliosResponse();
    if (CreateBasketRequestDto.PaymentOptionEnum.PAY_NOW.getValue().equals(basket.getPaymentOption().toString())
            && Objects.nonNull(depositFoliosDetails)) {
      basketOutPort.saveCharges(rsvDetails.getReservationByIdList().get(0).getDepositFoliosResponse());
    }
    var bookingAllowancesDetails = rsvDetails.getReservationByIdList().get(0)
            .getBookingAllowancesResponse();
    if (Objects.nonNull(bookingAllowancesDetails)
            && Objects.nonNull(bookingAllowancesDetails.getBookingAllowances())) {
      basketOutPort.updateAllowances(basket.getReference(),
              ReservationUtils.buildBasketBookingAllowances(
                      mapToInModel(bookingAllowancesDetails.getBookingAllowances())), basket.getETag());
    }

    if (isOta) {
      basketOutPort.changeIdContext(basket.getBookingReference(), ID_CONTEXT);
    }

    return basket;
  }

  private String getChannel(String sourceCode) {
    var channelRuleResponse = rulesOutPort.getChannelBasedOnSourceId(sourceCode);
    var channel = Optional.ofNullable(channelRuleResponse)
            .map(ChannelRuleResponse::getRequestDetails)
            .map(ChannelRuleRequestDetails::getChannel)
            .orElse(null);
    return FRONT_DESK_BOOKING_CHANNEL.equals(channel) ? PI_BOOKING_CHANNEL : channel;
  }

  private List<BusinessAllowance> mapToInModel(List<BookingAllowance> bookingAllowances) {
    return bookingAllowances.stream().map(
            allowanceDetails -> BusinessAllowance.builder().allowance(allowanceDetails.getAllowance())
                    .budget(allowanceDetails.getBudget()).build()).toList();
  }

  public String replaceHotelId(String operaConfNumber) {
    if (operaConfNumber.matches("^[A-Za-z]{6}\\d+$")) {
      return operaConfNumber.replaceAll("^[A-Za-z]+", "");
    }
    return operaConfNumber;
  }

  public boolean isDigitalReference(String bookingReference) {
    if (Objects.nonNull(bookingReference)) {
      return bookingReference.matches("^[A-Z]{3}\\d{7}$");
    }
    return false;
  }

  public boolean isOperaConfirmationNumber(String bookingReference) {
    if (Objects.nonNull(bookingReference)) {
      return bookingReference.matches("\\d+");
    }
    return false;
  }

  public boolean operaUiRsv(String bookingReference) {
    return bookingReference.matches("\\d+") || bookingReference.matches("^[A-Za-z]{6}\\d+$");
  }

  public boolean isSearchFlowEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getPiSearchByOperaConfirmation());
  }

  public boolean isOtaFlowEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMobileAcceptsOtaBooking());
  }

  public boolean is3rdPartyBooking(final String idContext, final String sourceCode) {
    if (isDesktopBooking(idContext, sourceCode)) {
      return false;
    }
    if (isDistributionBooking(sourceCode)) {
      return true;
    }
    if (isDigitalBooking(idContext, sourceCode)) {
      return false;
    }
    return true;
  }

  public boolean isDigitalBooking(final String idContext, final String sourceCode) {
    return WB_DIGITAL.equals(idContext) && (sourceCode != null) && (!sourceCode.equals(SOURCE_35))
        && (!sourceCode.equals(SOURCE_38)) && (!sourceCode.equals(SOURCE_43));
  }

  public boolean isDesktopBooking(final String idContext, final String sourceCode) {
    return
        Objects.isNull(idContext)
            && (sourceCode != null) && (sourceCode.equals(SOURCE_01) || sourceCode.equals(SOURCE_31));
  }

  public boolean isDistributionBooking(final String sourceCode) {
    return
        sourceCode != null && (sourceCode.equals(SOURCE_35) || sourceCode.equals(SOURCE_38)
            || sourceCode.equals(SOURCE_43));
  }

  /**
   * Should skip matching opera reservation details for bookings queried from Kiosk.
   * This is needed because Kiosk requests do not provide the lastName and arrivalDate.
   * This method will evaluate to true only for channel=KIOSK and subchannel=WEB, combination which
   * is requested only from kiosk-facade service.
   */
  public boolean shouldBypassMatchesOpera(BookingChannel bookingChannel) {
    return bookingChannel.isKiosk() && bookingChannel.isWebSubchannel();
  }

  /**
   * Build FindBookingResponse for 3rd party and digital bookings.
   * For a third-party booking, update the idContext in the basket to "3rd party" if it is not already saved.
   * If third-party bookings -distribution bookings are excepted - are not allowed, throws an exception.
   *
   * @param findBookingRequest findBookingRequest
   * @param bookingChannel     bookingChannel
   * @param operaIdContext     idContext from opera
   * @param sourceCode         sourcecode from opera
   * @param basket             basket
   * @return FindBookingResponse
   */
  public Optional<FindBookingResponse> getFindBookingResponse(
      final FindBookingRequest findBookingRequest, final BookingChannel bookingChannel,
      final String operaIdContext, final String sourceCode, final BasketResponse basket) {

    var isOtaFlag = isOtaFlowEnabled();
    if ("DISTR".equals(bookingChannel.getChannel())) {
      return buildFindBookingResponse(findBookingRequest, basket);
    }
    //check if it ota
    if (is3rdPartyBooking(operaIdContext, sourceCode)) {
      if (isOtaBooking(bookingChannel, operaIdContext,
          otaBookingProperties.getProvidersExcluded(), otaBookingProperties.getSubchannel(),
          isOtaFlag)) {
        basketOutPort.changeIdContext(basket.getBookingReference(), ID_CONTEXT);
        return buildFindBookingResponse(findBookingRequest, basket, ID_CONTEXT);
      } else {
        var exception = new GenericBadRequestException(ErrorCode.DIGITAL_INVALID_OTA_EXCEPTION,
            THIRD_PARTY_RESERVATION_IS_NOT_ALLOWED);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
    return buildFindBookingResponse(findBookingRequest, basket);
  }

  public Optional<FindBookingResponse> buildFindBookingResponse(
      FindBookingRequest findBookingRequest,
      BasketResponse basket) {
    return buildFindBookingResponse(findBookingRequest, basket, basket.getIdContext());
  }

  public Optional<FindBookingResponse> buildFindBookingResponse(
      FindBookingRequest findBookingRequest,
      BasketResponse basket, String idContext) {
    var bookingResponse = FindBookingResponse.builder()
        .sourcePms(OPERA)
        .ref(basket.getBookingReference())
        .basketReference(basket.getReference())
        .token(TokenUtils.getToken(basket.getReference()))
        .hotelId(basket.getHotelId())
        .idContext(idContext);

    applyDashboardRedirect(findBookingRequest, basket.getBookingReference(), bookingResponse);

    if (operaUiRsv(findBookingRequest.getResNo())) {
      bookingResponse.operaConfNumber(findBookingRequest.getResNo().replaceAll("^[A-Z]+", ""));
    }
    return Optional.ofNullable(bookingResponse.build());
  }

  public Optional<FindBookingResponse> buildReservationFindBookingResponse(
      FindBookingRequest findBookingRequest,
      BasketDto basket) {
    FindBookingResponse bookingResponse = FindBookingResponse.builder()
        .sourcePms(OPERA)
        .ref(basket.getBookingReference())
        .basketReference(basket.getReference())
        .token(TokenUtils.getToken(basket.getReference()))
        .hotelId(basket.getHotelId())
        .idContext(basket.getIdContext())
        .build();

    applyDashboardRedirect(findBookingRequest, basket.getBookingReference(), bookingResponse);

    if (operaUiRsv(findBookingRequest.getResNo())) {
      bookingResponse.setOperaConfNumber(findBookingRequest.getResNo().replaceAll("^[A-Z]+", ""));
    }
    return Optional.ofNullable(bookingResponse);
  }

  public boolean is3rdPartyBookingFlow(
      String idContextFromOpera,
      String sourceCode,
      BookingChannel bookingChannel) {
    var isOtaFlag = isOtaFlowEnabled();
    return idContextFromOpera != null
        && is3rdPartyBooking(idContextFromOpera, sourceCode)
        && isOtaBooking(bookingChannel, idContextFromOpera,
        otaBookingProperties.getProvidersExcluded(),
        otaBookingProperties.getSubchannel(), isOtaFlag);
  }

  private void applyDashboardRedirect(
      FindBookingRequest findBookingRequest,
      String bookingReference,
      FindBookingResponse.FindBookingResponseBuilder bookingResponseBuilder) {
    getDashboardRedirect(findBookingRequest, bookingReference)
        .ifPresent(dashboardRedirect -> {
          bookingResponseBuilder.redirectBase(dashboardRedirect.getOperaUrl());
          var cookie = dashboardRedirect.getCookie();
          if (cookie != null) {
            bookingResponseBuilder.cookieName(cookie.getName())
                .minutesTillExpiry(cookie.getMinutesTillExpiry());
          }
        });
  }

  private void applyDashboardRedirect(
      FindBookingRequest findBookingRequest,
      String bookingReference,
      FindBookingResponse bookingResponse) {
    getDashboardRedirect(findBookingRequest, bookingReference)
        .ifPresent(dashboardRedirect -> {
          bookingResponse.setRedirectBase(dashboardRedirect.getOperaUrl());
          var cookie = dashboardRedirect.getCookie();
          if (cookie != null) {
            bookingResponse.setCookieName(cookie.getName());
            bookingResponse.setMinutesTillExpiry(cookie.getMinutesTillExpiry());
          }
        });
  }

  private Optional<DashboardRedirect> getDashboardRedirect(
      FindBookingRequest findBookingRequest,
      String bookingReference) {
    try {
      return Optional.ofNullable(contentOutPort.getIndexHeaderData(findBookingRequest.getCountry(),
              findBookingRequest.getLanguage()))
          .map(IndexHeaderData::getConfig)
          .map(Config::getBookingSearch)
          .map(BookingSearch::getDashboardRedirect);
    } catch (ContentException | NoHeaderDataException exp) {
      log.error(
          "Index header data not found for country {} and language {}, "
              + "while building find booking response for reservation {}",
          findBookingRequest.getCountry(), findBookingRequest.getLanguage(),
          bookingReference);
      return Optional.empty();
    }
  }
}
