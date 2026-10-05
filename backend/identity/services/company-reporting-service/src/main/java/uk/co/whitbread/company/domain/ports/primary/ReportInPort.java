package uk.co.whitbread.company.domain.ports.primary;

import org.springframework.http.HttpHeaders;
import uk.co.whitbread.company.domain.model.in.ManagementInformationRequest;
import uk.co.whitbread.company.domain.model.out.EmergencyReportResult;
import uk.co.whitbread.company.domain.model.out.MiReportResult;

public interface ReportInPort {

  byte[] getManagementInformationReport(ManagementInformationRequest managementInformationRequest);

  HttpHeaders getReportHeaders(ManagementInformationRequest managementInformationRequest);

  MiReportResult getManagementInformationReportUrl(ManagementInformationRequest request);

  void validateDateRange(ManagementInformationRequest request);

  EmergencyReportResult getEmergencyReportUrl(String companyId, String accessContext,
      String accessedBy, String language);
}
