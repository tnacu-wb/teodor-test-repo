package uk.co.whitbread.ohip.domain.model.lov.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancellationReasonsResponse {

  private List<CancellationReason> cancellationReasons;

}
