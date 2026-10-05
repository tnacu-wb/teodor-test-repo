package uk.co.whitbread.cdh.domain.model.account.out.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEmployeesResponse {

  @JsonProperty("ContinuationToken")
  private String continuationToken;
  @JsonProperty("TotalEmployeesInCompany")
  private Integer totalEmployeesInCompany;
  @JsonProperty("Results")
  private List<GetEmployeeResponse> results;
}