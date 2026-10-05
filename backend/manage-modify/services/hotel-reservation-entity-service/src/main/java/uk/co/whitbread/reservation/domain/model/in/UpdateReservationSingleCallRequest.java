package uk.co.whitbread.reservation.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReservationSingleCallRequest {

  private BusinessItemsRequest businessItem;
  private ReservationGuestRequest reservationGuestDetails;
  private ReservationPackagesRequest reservationPackages;
  private ConfirmReservationRequest paymentDetails;
  private SpecialRequests specialRequests;

}
