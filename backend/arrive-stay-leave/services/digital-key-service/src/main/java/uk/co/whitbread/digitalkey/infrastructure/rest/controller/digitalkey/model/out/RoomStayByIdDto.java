package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomStayByIdDto {

  private String roomType;
  private String arrivalDate;
  private String departureDate;
  private String checkInTime;
  private String checkOutTime;
  private String roomNumber;

}
