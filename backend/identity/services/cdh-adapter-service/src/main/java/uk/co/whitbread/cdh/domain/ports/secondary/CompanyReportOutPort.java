package uk.co.whitbread.cdh.domain.ports.secondary;

import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;

public interface CompanyReportOutPort {

  CompanyReports getManagementInformation(ManagementInformation managementInformation,
      String companyId, String accessedBy, String accessContext);

  EmergencyReportResults getEmergencyReport(EmergencyReport emergencyReport,
      String companyId, String accessedBy, String accessContext);

}
