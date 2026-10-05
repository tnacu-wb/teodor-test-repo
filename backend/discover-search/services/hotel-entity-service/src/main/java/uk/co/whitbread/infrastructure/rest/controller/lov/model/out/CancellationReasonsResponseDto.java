package uk.co.whitbread.infrastructure.rest.controller.lov.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancellationReasonsResponseDto {

  private List<CancellationReasonDto> cancellationReasons;

}
