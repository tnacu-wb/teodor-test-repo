package uk.co.whitbread.cdh.infrastructure.rest.client.report.managementinformation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;
import uk.co.whitbread.cdh.domain.ports.secondary.CompanyReportOutPort;


@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyReportOutPortImpl implements CompanyReportOutPort {

  private final CompanyReportClient companyReportClient;

  @Override
  public CompanyReports getManagementInformation(ManagementInformation managementInformation,
      String companyId, String accessedBy, String accessContext) {
    return companyReportClient.getManagementInformation(managementInformation, companyId, accessedBy, accessContext);
  }

  @Override
  public EmergencyReportResults getEmergencyReport(EmergencyReport emergencyReport, String companyId,
      String accessedBy, String accessContext) {
    return companyReportClient.getEmergencyReport(emergencyReport, companyId, accessedBy, accessContext);
  }
}
