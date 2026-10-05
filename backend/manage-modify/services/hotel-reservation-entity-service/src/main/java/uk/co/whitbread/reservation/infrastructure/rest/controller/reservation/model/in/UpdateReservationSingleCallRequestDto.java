package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationSingleCallRequestDto {

  @JsonProperty("reservationGuestDetails")
  private ReservationGuestRequestDto reservationGuestDetails;
  @JsonProperty("reservationPackages")
  private ReservationPackagesRequestDto reservationPackages;
  @JsonProperty("businessItem")
  private BusinessItemsRequestDto businessItem;
  @JsonProperty("specialRequests")
  private SpecialRequestsDto specialRequests;
  @JsonProperty("paymentDetails")
  private ConfirmReservationRequestDto paymentDetails;

}
