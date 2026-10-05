package uk.co.whitbread.reservation.domain.logic.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.InvalidSourceCodeException;
import uk.co.whitbread.reservation.domain.model.basket.allowances.UpdateAllowancesRequest;
import uk.co.whitbread.reservation.domain.model.in.BusinessAllowance;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationCreationResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationInfo;
import uk.co.whitbread.reservation.domain.model.out.Reservations;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationUtils {

  public static String getReservationSourceCode(ReservationByBasketRefResponse rsvDetails) {
    String sourceCode = Optional.ofNullable(rsvDetails)
        .map(ReservationByBasketRefResponse::getReservationByIdList)
        .filter(CollectionUtils::isNotEmpty)
        .map(list -> list.get(0))
        .map(ReservationByIdResponse::getRoomStay)
        .map(RoomStayByIdResponse::getSourceCode)
        .orElse(null);
    verifySourceCode(sourceCode);

    return sourceCode;
  }

  public static String getReservationSourceCode(ReservationsDetailsEnhancedResponse operaResponse) {
    String sourceCode = Optional.ofNullable(operaResponse)
        .map(ReservationsDetailsEnhancedResponse::getReservations)
        .map(Reservations::getReservationInfo)
        .filter(CollectionUtils::isNotEmpty)
        .map(list -> list.get(0))
        .map(ReservationInfo::getRoomStay)
        .map(RoomStay::getSourceCode)
        .orElse(null);
    verifySourceCode(sourceCode);

    return sourceCode;
  }

  private static void verifySourceCode(String sourceCode) {
    if (StringUtils.isEmpty(sourceCode)) {
      var exception = new InvalidSourceCodeException(ErrorCode.DIGITAL_NOT_FOUND_SOURCE_EXCEPTION,
          "The source code for this reservation was not found.");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  public static OhipReservationResponse toOhipReservation(
      ReservationsDetailsEnhancedResponse reservationsDetailsResponse) {
    OhipReservationResponse ohipReservationResponse = new OhipReservationResponse();
    final List<OhipReservationCreationResponse> reservations = new LinkedList<>();
    ReservationInfo reservationInfo =
        reservationsDetailsResponse.getReservations().getReservationInfo().get(0);
    ohipReservationResponse.setHotelId(reservationInfo.getHotelId());
    ohipReservationResponse.setCurrencyCode(reservationsDetailsResponse.getCurrencyCode());
    ohipReservationResponse.setTotalCost(reservationsDetailsResponse.getTotalCost());

    reservationsDetailsResponse.getReservations().getReservationInfo().forEach(resInfo -> {
      OhipReservationCreationResponse ohipReservationCreationResponse =
          OhipReservationCreationResponse.builder()
              .reservationId(resInfo.getReservationIdList().get(0).getId())
              .createDateTime(DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()))
              .roomStay(resInfo.getRoomStay())
              .build();

      reservations.add(ohipReservationCreationResponse);
    });
    ohipReservationResponse.setReservations(reservations);

    return ohipReservationResponse;
  }

  public static CopyReservationsResponse toOhipReservation(ReservationByBasketRefResponse reservationsDetails) {
    CopyReservationsResponse ohipReservationResponse = new CopyReservationsResponse();
    final List<CopyReservationResponse> reservations = new LinkedList<>();
    reservationsDetails.getReservationByIdList().forEach(resInfo -> {
      CopyReservationResponse ohipReservationCreationResponse =
          getOhipReservationCreationResponse(resInfo.getReservationId());

      reservations.add(ohipReservationCreationResponse);
    });
    ohipReservationResponse.setReservations(reservations);

    return ohipReservationResponse;
  }

  public static CopyReservationResponse getOhipReservationCreationResponse(String rsvId) {
    return CopyReservationResponse.builder()
        .reservationId(rsvId)
        .createDateTime(DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()))
        .build();
  }

  public static UpdateAllowancesRequest buildBasketBookingAllowances(
      List<BusinessAllowance> businessAllowances) {
    return UpdateAllowancesRequest.builder()
        .bookingAllowances(businessAllowances.stream().map(businessAllowance -> BookingAllowance.builder()
                .allowance(businessAllowance.getAllowance())
                .budget(businessAllowance.getBudget())
                .build())
            .toList())
        .build();
  }



}
