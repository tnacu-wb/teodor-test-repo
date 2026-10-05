package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.out.DepositFoliosResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationByIdDto {

  String reservationId;
  List<ReservationByIdGuestsDto> reservationGuestList;
  ReservationBookerDto reservationBooker;
  ReservationCompanyDto reservationCompany;
  RoomStayByIdDto roomStay;
  RateInfoDto rateInfo;
  AdditionalGuestInfoDto additionalGuestInfo;
  List<DepositPoliciesDto> depositPolicies;
  BillingResponseDto billing;
  List<ReservationPackagesDetailsResponseDto> reservationPackageList;
  ReservationPaymentCardTypeDto paymentCard;
  ReservationOverrideReasonsDto reservationOverrideReasons;
  boolean reservationOverridden;
  GuaranteeDto guarantee;
  String reservationStatus;
  ReservationEmailNotificationsDto reservationEmailNotifications;
  BigDecimal balanceAmount;
  String gdsReferenceNumber;
  ResCashieringTypeDto cashiering;
  UserDefinedFieldsDto userDefinedFields;
  BookingAllowancesResponseDto bookingAllowancesResponse;
  Boolean operaLinkedReservation;
  DepositFoliosResponseDto depositFoliosResponse;
  Boolean preCheckInStatus;
  private List<ReservationEventPreferenceDto> preferences;
  private List<ReservationAlertsDto> alerts;
}
