package uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyReportBookerDto {

  private String emailAddress;
  private String telephoneNumber;
  private String mobileNumber;

}
