package uk.co.whitbread.company.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import uk.co.whitbread.company.domain.model.in.ManagementInformationRequest;
import uk.co.whitbread.company.domain.model.out.EmergencyReportResult;
import uk.co.whitbread.company.domain.model.out.MiReportResult;
import uk.co.whitbread.company.domain.ports.primary.ReportInPort;
import uk.co.whitbread.company.domain.ports.secondary.ReportOutPort;

@Slf4j
@RequiredArgsConstructor
public class ReportingPortBusinessCase implements ReportInPort {

  private final ReportOutPort reportOutPort;

  @Override
  public byte[] getManagementInformationReport(ManagementInformationRequest managementInformationRequest) {
    log.debug("Entered getManagementInformationReport for companyId={}", managementInformationRequest.companyId());
    return reportOutPort.getManagementInformationReport(managementInformationRequest);
  }

  @Override
  public HttpHeaders getReportHeaders(ManagementInformationRequest managementInformationRequest) {
    log.debug("Entered getReportHeaders for companyId={}", managementInformationRequest.companyId());
    return reportOutPort.getHeaders(managementInformationRequest);
  }

  @Override
  public MiReportResult getManagementInformationReportUrl(ManagementInformationRequest request) {
    log.debug("Entered getManagementInformationReportURL for companyId={}", request.companyId());
    return reportOutPort.getManagementInformationReportUrl(request);
  }

  @Override
  public void validateDateRange(ManagementInformationRequest request) {
    reportOutPort.validateDateRange(request);
  }

  @Override
  public EmergencyReportResult getEmergencyReportUrl(String companyId, String accessContext,
      String accessedBy, String language) {
    log.debug("Entered getEmergencyReportUrl for companyId={}", companyId);
    return reportOutPort.getEmergencyReportUrl(companyId, accessContext, accessedBy, language);

  }


}
