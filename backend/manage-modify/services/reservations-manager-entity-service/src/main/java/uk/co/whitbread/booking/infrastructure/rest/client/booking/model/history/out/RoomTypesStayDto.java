package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out;

import lombok.Data;

@Data
public class RoomTypesStayDto {

  private String roomType;
  private String bookingStatus;
  private boolean carDataPresent;
  private PersonDetailsDto personDetails;
}
