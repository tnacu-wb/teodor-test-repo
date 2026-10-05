package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.ohip.domain.model.checkin.out.UserDefinedFields;

@Data
@Builder
@AllArgsConstructor
public class ReservationById {

  String hotelId;
  String reservationId;
  List<ReservationGuest> reservationGuestList;
  ReservationBooker reservationBooker;
  ReservationCompany reservationCompany;
  RoomStay roomStay;
  RateInfo rateInfo;
  List<DepositPolicies> depositPolicies;
  AdditionalGuestInfo additionalGuestInfo;
  BillingResponse billing;
  List<ReservationPackagesDetailsResponse> reservationPackageList;
  ReservationPaymentCardType paymentCard;
  ReservationOverrideReasons reservationOverrideReasons;
  boolean reservationOverridden;
  Guarantee guarantee;
  String reservationStatus;
  ReservationEmailNotifications reservationEmailNotifications;
  BigDecimal balanceAmount;
  String gdsReferenceNumber;
  ResCashieringType cashiering;
  UserDefinedFields userDefinedFields;
  BookingAllowancesResponse bookingAllowancesResponse;
  Boolean operaLinkedReservation;
  DepositFoliosResponse depositFoliosResponse;
  Boolean preCheckInStatus;
  List<ReservationEventPreference> preferences;
  List<ReservationAlerts> alerts;
}
