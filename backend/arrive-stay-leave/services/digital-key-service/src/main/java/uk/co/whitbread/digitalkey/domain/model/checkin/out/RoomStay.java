package uk.co.whitbread.digitalkey.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomStay {

  private RegistrationNumber registrationNumber;
  private CurrentRoomInfo currentRoomInfo;
  private String arrivalDate;
  private String departureDate;
  private ExpectedTimes expectedTimes;
  private boolean roomNumberLocked;
  private boolean printRate;

}
