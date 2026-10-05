package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationSingleCallRequestDto {

  private BusinessItemsRequestDto businessItem;
  private ReservationGuestRequestDto reservationGuestDetails;
  private ReservationPackagesRequestDto reservationPackages;
  private ConfirmReservationRequestDto paymentDetails;
  private SpecialRequestsDto specialRequests;

}
