package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationInfo {

  private List<UniqueIDType> reservationIdList;
  private List<ExternalReferenceType> externalReferences;
  private RoomStay roomStay;
  private ReservationGuest reservationGuest;
  private String hotelId;
  private String hotelName;
  private boolean roomStayReservation;
  private String reservationStatus;
}
