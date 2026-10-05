package uk.co.whitbread.booking.domain.model.history.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomTypes {

  private String roomType;
  private String bookingStatus;
  private boolean carDataPresent;
  private PersonalDetails personDetails;
}
