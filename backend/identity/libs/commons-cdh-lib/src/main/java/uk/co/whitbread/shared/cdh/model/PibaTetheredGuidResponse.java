package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PibaTetheredGuidResponse {

  @JsonProperty("CompanyId")
  private Integer companyId;

  @JsonProperty("EmployeeId")
  private Integer employeeId;

  @JsonProperty("Scheme")
  private String scheme;

  @JsonProperty("TetheredGuid")
  private String tetheredGuid;
}
