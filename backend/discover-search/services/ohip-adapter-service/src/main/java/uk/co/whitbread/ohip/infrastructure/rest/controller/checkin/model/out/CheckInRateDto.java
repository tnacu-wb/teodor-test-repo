package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRateDto {

  private BaseDto base;
  private String shareDistributionInstruction;
  private TotalDto total;
  private String start;
  private String end;

}
