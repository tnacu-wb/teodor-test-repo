package uk.co.whitbread.kiosk.domain.logic;

import static uk.co.whitbread.kiosk.ErrorCode.KIOSK_CARD_EXCEPTION;
import static uk.co.whitbread.kiosk.ErrorCode.KIOSK_OUTSTANDING_BALANCE_NOT_PAID_EXCEPTION;
import static uk.co.whitbread.kiosk.ErrorCode.KIOSK_PARTIAL_PAID_CHECK_IN_EXCEPTION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.kiosk.domain.logic.config.PaymentTypeConfig;
import uk.co.whitbread.kiosk.domain.logic.exception.BalanceNotPaidException;
import uk.co.whitbread.kiosk.domain.logic.exception.ConfirmReservationException;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.GuestDetails;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ProfileRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.StayingGuestDetails;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.in.ConfirmReservationPaymentCard;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.in.ConfirmReservationRequest;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HouseKeepingResponse;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.kiosk.domain.ports.primary.KioskInPort;
import uk.co.whitbread.kiosk.domain.ports.secondary.KioskOutPort;

@AllArgsConstructor
@Slf4j
public class KioskInPortImpl implements KioskInPort {

  private final KioskOutPort kioskOutPort;
  private final PaymentTypeConfig paymentTypeConfig;

  @Override
  public CheckInResponse getCheckInResponse(CheckInRequest checkInRequest) {

    kioskOutPort.processProfileRequest(checkInRequest);

    if (null != checkInRequest.getReservationComments() && !checkInRequest.getReservationComments()
        .isEmpty()) {
      kioskOutPort.updateReservationComments(checkInRequest.getHotelId(),
          checkInRequest.getReservationNumber(), checkInRequest.getReservationComments());
    }

    kioskOutPort.getOutstandingBalance(checkInRequest.getReservationNumber(), checkInRequest.getHotelId())
        .ifPresent(outstandingBalance -> {
          if (outstandingBalance.compareTo(checkInRequest.getPaymentDetails().getAmount()) != 0) {
            BalanceNotPaidException exception =
                new BalanceNotPaidException(KIOSK_OUTSTANDING_BALANCE_NOT_PAID_EXCEPTION,
                    "Outstanding balance is not fully covered");
            ExceptionLogger.log(log, exception);
            throw exception;
          }
        });

    if (Objects.nonNull(checkInRequest.getPaymentDetails())
        && Objects.nonNull(checkInRequest.getPaymentDetails().getAmount())
        && BigDecimal.ZERO.compareTo(checkInRequest.getPaymentDetails().getAmount()) != 0) {
      if (null != checkInRequest.getPaymentDetails().getCard()) {
        final var confirmReservationRequest = prepareConfirmReservationRequest(
            checkInRequest);
        final var confirmReservationResponse = kioskOutPort.confirmReservation(
            confirmReservationRequest);
        if (confirmReservationResponse.isPartialPaid()) {
          ConfirmReservationException exception =
              new ConfirmReservationException(KIOSK_PARTIAL_PAID_CHECK_IN_EXCEPTION,
                  "Partial paid reservations are not allowed to check-in via Kiosk");
          ExceptionLogger.log(log, exception);
          throw exception;
        }
      } else {
        ConfirmReservationException exception =
            new ConfirmReservationException(KIOSK_CARD_EXCEPTION, "Missing Card Details");
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
    return kioskOutPort.doCheckIn(checkInRequest);
  }

  private ConfirmReservationRequest prepareConfirmReservationRequest(
      CheckInRequest checkInRequest) {
    final String cardTypeFrmRequest = checkInRequest.getPaymentDetails().getCard().getCardType();
    final String cardType = cardTypeFrmRequest
        .equalsIgnoreCase(CommonConstants.ZZ) ? CommonConstants.BU : cardTypeFrmRequest;
    return ConfirmReservationRequest.builder()
        .hotelId(checkInRequest.getHotelId())
        .reservationId(checkInRequest.getReservationNumber())
        .paymentOption("PAY_NOW")
        .paymentMethod(paymentTypeConfig.getType()
            .get(checkInRequest.getPaymentDetails().getCard().getCardSchemeId()))
        .paymentType(cardType)
        .paymentCard(ConfirmReservationPaymentCard.builder()
            .cardType(cardType)
            .cardHolderName(checkInRequest.getPaymentDetails().getCard().getCardholderName())
            .token(checkInRequest.getPaymentDetails().getCard().getToken())
            .expirationDate(StringUtils.joinWith("-",
                checkInRequest.getPaymentDetails().getCard().getExpiryYear(),
                checkInRequest.getPaymentDetails().getCard().getExpiryMonth(), "28"))
            .cardNumberLast4Digits(checkInRequest.getPaymentDetails().getCard().getType())
            .build()).build();
  }

  @Override
  public ProfileRequest createProfileRequest(CheckInRequest checkInRequest) {
    List<StayingGuestDetails> stayingGuestDetails = checkInRequest.getStayingGuestDetails();
    List<GuestDetails> guestDetails = new ArrayList<>();
    for (StayingGuestDetails guestDetail : stayingGuestDetails) {
      guestDetails.add(GuestDetails.builder().nameTitle(guestDetail.getTitle()).givenName(
          guestDetail.getFirstName()).surname(guestDetail.getLastName()).build());
    }
    return ProfileRequest.builder().guestDetails(guestDetails).build();
  }

  @Override
  public AllocationResponse allocateRooms(String hotelId, String reservationId,
      VacantRoomResponse vacantRoomResponse, String roomType,
      KioskReservationPreferences reservationPreferencesResponse) {
    return kioskOutPort.allocateRooms(hotelId, reservationId, vacantRoomResponse,
        roomType, reservationPreferencesResponse);
  }

  @Override
  public VacantRoomResponse getVacantRooms(String hotelId, String roomType) {
    return kioskOutPort.getVacantRooms(hotelId, roomType);
  }

  @Override
  public HouseKeepingResponse fetchHouseKeepingStatus(String hotelId, String roomId) {
    return kioskOutPort.fetchHouseKeepingStatus(hotelId, roomId);
  }

  @Override
  public KioskReservationPreferences fetchReservationPreferences(String hotelId, String roomId) {
    return kioskOutPort.fetchReservationPreferences(hotelId, roomId);
  }

}
