package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import java.util.List;
import lombok.Data;

@Data
public class ReservationDto {

  private String reservationId;
  private List<ReservationGuestsDto> reservationGuestList;
  private RoomStayDto roomStay;
  private List<DepositPoliciesDto> depositPolicies;
  private BillingDto billing;
  private PaymentCardDto paymentCard;
  private List<ReservationPackagesDetailsDto> reservationPackageList;
  private RateInfoDto rateInfo;
}
