package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.checkin.out.PackageHeaderType;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPackages {

  private List<ScheduleList> scheduleList;
  private ConsumptionDetails consumptionDetails;
  private String packageCode;
  private String packageGroup;
  private String ratePlanCode;
  private String startDate;
  private String endDate;
  private PackageHeaderType packageHeaderType;

}
