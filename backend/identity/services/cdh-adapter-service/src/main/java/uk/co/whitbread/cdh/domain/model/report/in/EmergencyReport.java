package uk.co.whitbread.cdh.domain.model.report.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyReport {

  @JsonProperty("FromDate")
  private String fromDate;

  @JsonProperty("ToDate")
  private String toDate;

}
