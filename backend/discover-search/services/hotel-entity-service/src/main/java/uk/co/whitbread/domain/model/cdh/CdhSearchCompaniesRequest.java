package uk.co.whitbread.domain.model.cdh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhSearchCompaniesRequest {

  private Integer globalCompanyId;
  private String accessContext;
  private String accessedBy;
}
