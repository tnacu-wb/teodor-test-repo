package uk.co.whitbread.employee.bulk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEmployeeSearchCriteria {
  
  private String companyAccountId;
  private Integer pageSize;
  private String pageToken;
  private Boolean awaitingApproval;
  private String accessedBy;
  private AccessLevel accessLevel;
}
