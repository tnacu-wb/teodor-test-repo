package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumptionDetailsDto {

  private int totalQuantity;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer defaultQuantity;

}
