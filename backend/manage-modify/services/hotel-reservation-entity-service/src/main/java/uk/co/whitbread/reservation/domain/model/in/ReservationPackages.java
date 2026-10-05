package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ReservationPackages {
  private List<ScheduleList> scheduleList;
  private ConsumptionDetails consumptionDetails;
  private String packageCode;
  private String startDate;
  private String endDate;
}
