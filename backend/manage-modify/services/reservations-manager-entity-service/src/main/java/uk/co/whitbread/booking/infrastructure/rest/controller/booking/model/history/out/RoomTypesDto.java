package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.out;

import lombok.Data;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out.PersonDetailsDto;

@Data
public class RoomTypesDto {

  private String roomType;
  private String bookingStatus;
  private boolean carDataPresent;
  private PersonDetailsDto personDetails;
}
