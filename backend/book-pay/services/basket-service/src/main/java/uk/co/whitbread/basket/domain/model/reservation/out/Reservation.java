package uk.co.whitbread.basket.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Reservation {

  private String reservationId;
  private List<Guest> reservationGuestList;
  private RoomStay roomStay;
  private List<DepositPolicies> depositPolicies;
  private Billing billing;
  private PaymentCard paymentCard;
  private List<ReservationPackagesDetails> reservationPackageList;
  private RateInfo rateInfo;
}

