package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnpRequest;
import uk.co.whitbread.reservation.domain.model.in.BusinessItemsRequest;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmendDistributionSingleCallRequestDto {
  private UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest;
  private BookerDetailsCnpRequest bookerDetailsCnpRequest;
  private List<SpecialRequests> specialRequests;
  private List<UpdateReservationsRequest> editRoomRequest;
  private UpdateReservationsRequest stayDateUpdateRequest;
  private BusinessItemsRequest bookingAllowancesRequest;
}