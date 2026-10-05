package uk.co.whitbread.spending.domain.model.out.cdh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TetheredGuidResponse {
  private String scheme;
  private String tetheredGuid;
}
