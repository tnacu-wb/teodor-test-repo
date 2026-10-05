package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryCountsTypeDto {

  @JsonProperty("available")
  private Boolean available;

  @JsonProperty("availableCount")
  private Integer availableCount;

  @JsonProperty("availableTentativeCount")
  private Integer availableTentativeCount;

  @JsonProperty("startDate")
  private LocalDate startDate;

  @JsonProperty("endDate")
  private LocalDate endDate;

}
