package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.SummaryDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceBreakdownOhipDto {

  @JsonProperty("summary")
  private SummaryDto summary;
  private String roomType;
  private Integer adults;
  private Integer children;
}
