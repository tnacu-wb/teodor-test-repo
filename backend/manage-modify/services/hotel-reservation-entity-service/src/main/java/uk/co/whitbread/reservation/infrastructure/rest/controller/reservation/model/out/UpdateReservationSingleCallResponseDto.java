package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BusinessItemsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ConfirmReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationGuestRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.SpecialRequestsDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationSingleCallResponseDto {
  private ReservationGuestRequestDto reservationGuestDetails;
  private ReservationPackagesRequestDto reservationPackages;
  private BusinessItemsRequestDto businessItem;
  private SpecialRequestsDto specialRequests;
  private ConfirmReservationRequestDto paymentDetails;
}
