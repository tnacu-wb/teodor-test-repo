package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationByIdDto {

  private String reservationId;
  private List<ReservationByIdGuestsDto> reservationGuestList;
  private ReservationBookerDto reservationBooker;
  private RoomStayByIdDto roomStay;
  private RateInfoDto rateInfo;
  private AdditionalGuestInfoDto additionalGuestInfo;
  private List<DepositPoliciesDto> depositPolicies;
  private BillingResponseDto billing;
  private List<ReservationPackagesDetailsResponseDto> reservationPackageList;
  private ReservationPaymentCardTypeDto paymentCard;
  private ReservationOverrideReasonsDto reservationOverrideReasons;
  private boolean reservationOverridden;
  private Boolean onHold;
  private String reservationStatus;
  private ReservationEmailNotificationsDto reservationEmailNotifications;
  private String guaranteeCode;
  private BigDecimal balanceAmount;
  private Boolean preCheckInStatus;

  @JsonInclude(Include.NON_NULL)
  private String gdsReferenceNumber;

  private List<ReservationPreferencesDto> preferences;
  private String wifiCode;
  private Boolean deRegCardCompleted;
}
