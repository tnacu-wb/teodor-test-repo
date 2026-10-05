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
public class ReservationByIdResponse {
  private String reservationId;
  private List<ReservationGuest> reservationGuestList;
  private ReservationBooker reservationBooker;
  private ReservationCompany reservationCompany;
  private RoomStayByIdResponse roomStay;
  private RateInfo rateInfo;
  private AdditionalGuestInfo additionalGuestInfo;
  private List<DepositPolicies> depositPolicies;
  private BillingResponse billing;
  private List<ReservationPackagesDetailsResponse> reservationPackageList;
  private ReservationPaymentCardType paymentCard;
  private ReservationOverrideReasons reservationOverrideReasons;
  private boolean reservationOverridden;
  private boolean onHold;
  private String reservationStatus;
  private ReservationEmailNotifications reservationEmailNotifications;
  private String guaranteeCode;
  private BigDecimal balanceAmount;
  private String gdsReferenceNumber;
  private ResCashieringType cashiering;
}