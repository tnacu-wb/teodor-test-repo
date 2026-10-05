package uk.co.whitbread.payapp.domain.model.out.cdh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TetheredGuidResponse {

  private Integer companyId;
  private Integer employeeId;
  private String scheme;
  private String tetheredGuid;
}
