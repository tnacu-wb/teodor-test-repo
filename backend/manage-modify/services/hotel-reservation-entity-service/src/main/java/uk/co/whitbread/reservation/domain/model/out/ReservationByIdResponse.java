package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationByIdResponse {

  private String reservationId;
  private List<ReservationByIdGuestsResponse> reservationGuestList;
  private ReservationBooker reservationBooker;
  private ReservationCompany reservationCompany;
  private RoomStayByIdResponse roomStay;
  private RateInfo rateInfo;
  private AdditionalGuestInfoResponse additionalGuestInfo;
  private List<DepositPoliciesResponse> depositPolicies;
  private BillingResponse billing;
  private List<ReservationPackagesDetailsResponse> reservationPackageList;
  private ReservationPaymentCardType paymentCard;
  private ReservationOverrideReasons reservationOverrideReasons;
  private boolean reservationOverridden;
  private boolean onHold;
  private String reservationStatus;
  private ReservationEmailNotificationsResponse reservationEmailNotifications;
  private String guaranteeCode;
  private BigDecimal balanceAmount;
  private String gdsReferenceNumber;
  private ResCashieringType cashiering;
  private UserDefinedFields userDefinedFields;
  private Boolean preCheckInStatus;
  private Boolean operaLinkedReservation;
  private BookingAllowancesResponse bookingAllowancesResponse;
  private DepositFoliosResponse depositFoliosResponse;
  private List<ReservationPreferencesResponse> preferences;
  private Boolean deRegCardCompleted;
}
