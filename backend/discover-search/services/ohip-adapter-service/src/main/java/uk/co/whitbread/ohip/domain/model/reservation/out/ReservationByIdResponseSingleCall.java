package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationByIdResponseSingleCall {
  private String reservationId;
  private List<ReservationByIdGuestsResponse> reservationGuestList;
  private ReservationBookerSingleCall reservationBooker;
  private ReservationCompany reservationCompany;
  private RoomStayByIdResponse roomStay;
  private RateInfoSingleCall rateInfo;
  private AdditionalGuestInfo additionalGuestInfo;
  private List<DepositPoliciesResponseSingleCall> depositPolicies;
  private BillingResponse billing;
  private List<ReservationPackagesDetailsResponse> reservationPackageList;
  private ReservationPaymentCardTypeSingleCall paymentCard;
  private ReservationOverrideReasons reservationOverrideReasons;
  private boolean reservationOverridden;
  private boolean onHold;
  private String reservationStatus;
  private ReservationEmailNotificationsSingleCall reservationEmailNotifications;
  private String guaranteeCode;
  private BigDecimal balanceAmount;
  private String gdsReferenceNumber;
  private ResCashieringTypeSingleCall cashiering;
}