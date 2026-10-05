package uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RatePlansResponseDto {

  private List<RatePlanDto> ratePlans;
}
