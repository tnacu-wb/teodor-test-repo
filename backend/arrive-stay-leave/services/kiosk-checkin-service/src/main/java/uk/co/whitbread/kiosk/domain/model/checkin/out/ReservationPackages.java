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
public class ReservationPackages {

  private PackageHeaderType packageHeaderType;
  private List<ScheduleList> scheduleList;
  private ConsumptionDetails consumptionDetails;
  private String packageCode;
  private double internalID;
  private String startDate;
  private String endDate;
  private String source;

}
