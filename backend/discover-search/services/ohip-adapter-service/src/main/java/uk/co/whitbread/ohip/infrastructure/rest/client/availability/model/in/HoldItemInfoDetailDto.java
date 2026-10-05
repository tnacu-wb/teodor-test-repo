package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoldItemInfoDetailDto {

  private String itemCode;
  private HoldItemTimeSpanDto timeSpan;
  private Integer count;

}
