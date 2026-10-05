package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostingAttributesDto {

  private boolean addToRate;
  private boolean printSeparateLine;
  private boolean postNextDay;
  private boolean forecastNextDay;

}
