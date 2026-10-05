package uk.co.whitbread.kiosk.domain.model.checkin.out;

import java.util.List;
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
  private List<RoomRates> roomRates;
  private GuestCounts guestCounts;
  private String arrivalDate;
  private String departureDate;
  private ExpectedTimes expectedTimes;
  private Guarantee guarantee;
  private Total total;
  private TotalPoints totalPoints;
  private boolean roomNumberLocked;
  private boolean printRate;

}
