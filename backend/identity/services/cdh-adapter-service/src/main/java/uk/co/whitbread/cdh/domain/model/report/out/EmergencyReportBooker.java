package uk.co.whitbread.cdh.domain.model.report.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyReportBooker {

  @JsonProperty("EmailAddress")
  private String emailAddress;

  @JsonProperty("TelephoneNumber")
  private String telephoneNumber;


  @JsonProperty("MobileNumber")
  private String mobileNumber;

}
