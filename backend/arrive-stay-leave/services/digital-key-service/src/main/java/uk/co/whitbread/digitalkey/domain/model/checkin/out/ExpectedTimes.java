package uk.co.whitbread.digitalkey.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpectedTimes {

  private String reservationExpectedArrivalTime;
  private String reservationExpectedDepartureTime;

}
