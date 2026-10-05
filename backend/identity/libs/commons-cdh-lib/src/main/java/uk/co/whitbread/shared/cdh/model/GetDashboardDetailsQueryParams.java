package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetDashboardDetailsQueryParams {

  private String employeeId;
  private String companyId;

}
