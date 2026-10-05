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
public class ReservationPackagesDto {

  private PackageHeaderTypeDto packageHeaderType;
  private List<ScheduleListDto> scheduleList;
  private ConsumptionDetailsDto consumptionDetails;
  private String packageCode;
  private double internalID;
  private String startDate;
  private String endDate;
  private String source;

}
