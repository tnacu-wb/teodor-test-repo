package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiOccupancySupplementRequestDto {

  @NotNull
  private List<OccupancySupplementRequestDto> hotelIds;
}
