package uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cdh.api")
public class CdhApiProperties {

  private String host;
  private String requestHeaderName;
  private String requestHeaderValue;
  private String getCompaniesEndpoint;
  private String getCompanyEmployeeEndpoint;
  private String companyEmployeesEndpointV2;
  private String getEmployeeEndpoint;
  private String employeeEndpointV2;
  private String getManagementInformationEndpoint;
  private String getEmergencyReportEndpoint;
  private String getEmployeesEndpoint;
  private String getCompanyEndpoint;
  private String employeesEndpointV2;
  private String getEmployeeSpendEndpoint;
  private String reservationSearchEndpointV3;
}
