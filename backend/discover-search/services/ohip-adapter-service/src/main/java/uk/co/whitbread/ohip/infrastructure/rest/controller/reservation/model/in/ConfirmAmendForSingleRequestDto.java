package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItemsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.SpecialRequests;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequestSingleCall;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmAmendForSingleRequestDto {
  UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest;
  BookerDetailsCnpRequest bookerDetailsCnpRequest;
  List<SpecialRequests> specialRequests;
  List<UpdateReservationsRequestSingleCall> editRoomRequest;
  UpdateReservationsRequestSingleCall stayDateUpdateRequest;
  BusinessItemsRequest bookingAllowancesRequest;

}