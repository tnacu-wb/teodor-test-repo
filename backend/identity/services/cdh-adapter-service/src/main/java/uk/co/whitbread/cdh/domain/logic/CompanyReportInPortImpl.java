package uk.co.whitbread.cdh.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;
import uk.co.whitbread.cdh.domain.ports.primary.CompanyReportInPort;
import uk.co.whitbread.cdh.domain.ports.secondary.CompanyReportOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class CompanyReportInPortImpl implements CompanyReportInPort {

  private final CompanyReportOutPort companyReportOutPort;

  @Override
  public CompanyReports getManagementInformation(ManagementInformation managementInformation,
      String companyId, String accessedBy, String accessContext) {
    return this.companyReportOutPort.getManagementInformation(managementInformation,
        companyId, accessedBy, accessContext);
  }

  @Override
  public EmergencyReportResults getEmergencyReport(EmergencyReport emergencyReport, String companyId,
      String accessedBy, String accessContext) {
    return this.companyReportOutPort.getEmergencyReport(emergencyReport,
        companyId, accessedBy, accessContext);
  }
} 
