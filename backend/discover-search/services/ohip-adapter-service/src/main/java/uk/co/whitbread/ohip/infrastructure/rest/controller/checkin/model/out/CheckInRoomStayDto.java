package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRoomStayDto {

  private RegistrationNumberDto registrationNumber;
  private CurrentRoomInfoDto currentRoomInfo;
  private List<RoomRatesDto> roomRates;
  private GuestCountsDto guestCounts;
  private String arrivalDate;
  private String departureDate;
  private ExpectedTimesDto expectedTimes;
  private CheckInGuaranteeDto guarantee;
  private TotalDto total;
  private TotalPointsDto totalPoints;
  private boolean roomNumberLocked;
  private boolean printRate;

}
