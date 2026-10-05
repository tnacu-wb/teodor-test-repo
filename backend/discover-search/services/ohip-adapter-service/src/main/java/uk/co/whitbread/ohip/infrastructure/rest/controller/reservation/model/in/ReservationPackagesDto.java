package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out.PackageHeaderTypeDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPackagesDto {

  private List<ScheduleListDto> scheduleList;
  private ConsumptionDetailsDto consumptionDetails;
  private String packageCode;
  private String startDate;
  private String endDate;
  private PackageHeaderTypeDto packageHeaderType;

}
