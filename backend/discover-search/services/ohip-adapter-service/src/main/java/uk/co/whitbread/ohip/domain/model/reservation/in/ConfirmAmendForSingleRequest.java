package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmAmendForSingleRequest {

  UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest;
  BookerDetailsCnpRequest bookerDetailsCnpRequest;
  List<SpecialRequests> specialRequests;
  List<UpdateReservationsRequestSingleCall> editRoomRequest;
  UpdateReservationsRequestSingleCall stayDateUpdateRequest;
  BusinessItemsRequest bookingAllowancesRequest;

}