package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmendDistributionSingleCallRequest {
  private UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest;
  private BookerDetailsCnpRequest bookerDetailsCnpRequest;
  private List<SpecialRequests> specialRequests;
  private List<UpdateReservationsRequest> editRoomRequest;
  private UpdateReservationsRequest stayDateUpdateRequest;
  private BusinessItemsRequest bookingAllowancesRequest;
}