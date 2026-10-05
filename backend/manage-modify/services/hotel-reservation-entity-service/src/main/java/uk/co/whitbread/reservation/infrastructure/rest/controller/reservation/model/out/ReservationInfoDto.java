package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ReservationInfoDto {

  private List<UniqueIDTypeDto> reservationIdList;
  private List<ExternalReferenceTypeDto> externalReferences;
  private RoomStayDto roomStay;
  private ReservationGuestDto reservationGuest;
  private String hotelId;
  private String hotelName;
  private boolean roomStayReservation;
}
