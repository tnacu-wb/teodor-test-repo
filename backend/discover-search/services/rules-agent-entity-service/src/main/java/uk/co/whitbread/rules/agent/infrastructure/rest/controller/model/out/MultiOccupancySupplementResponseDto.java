package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiOccupancySupplementResponseDto {

  private Map<String, BigDecimal> dictionary;
  private List<OccupancySupplementResponseDto> list;
}
