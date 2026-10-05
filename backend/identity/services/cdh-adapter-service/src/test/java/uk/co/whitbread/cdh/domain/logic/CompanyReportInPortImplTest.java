package uk.co.whitbread.cdh.domain.logic;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyResults;
import uk.co.whitbread.cdh.domain.model.report.out.Reports;
import uk.co.whitbread.cdh.domain.ports.secondary.CompanyReportOutPort;

@ExtendWith(MockitoExtension.class)
class CompanyReportInPortImplTest {

  @InjectMocks
  private CompanyReportInPortImpl companyReportInPortImpl;

  @Mock
  private CompanyReportOutPort companyReportOutPort;

  @Test
  void testGetManagementInformation() {

    Reports reports = Reports.builder().arrivalDate("2023-01-01").build();
    CompanyReports companyReports = CompanyReports.builder().results(List.of(reports))
        .build();

    when(companyReportOutPort.getManagementInformation(ManagementInformation.builder()
            .fromDate("2023-01-01").toDate("2023-01-10").build(), "COMP_cdc002ca-a3a0-4226-a8ff-cb27ae32262d",
        "cdh-adapter-service", "ccui"))
        .thenReturn(companyReports);

    var response = companyReportInPortImpl.getManagementInformation(ManagementInformation.builder()
            .fromDate("2023-01-01").toDate("2023-01-10").build(),
        "COMP_cdc002ca-a3a0-4226-a8ff-cb27ae32262d",
        "cdh-adapter-service", "ccui");

    assertNotNull(response);
  }

  @Test
  void testGetEmergencyReport() {

    EmergencyResults reports = EmergencyResults.builder().arrivalDate("2023-01-01").build();
    EmergencyReportResults companyReports = EmergencyReportResults.builder().results(List.of(reports))
        .build();

    when(companyReportOutPort.getEmergencyReport(EmergencyReport.builder()
            .fromDate("2023-01-01").toDate("2023-01-10").build(), "COMP_cdc002ca-a3a0-4226-a8ff-cb27ae32262d",
        "cdh-adapter-service", "ccui"))
        .thenReturn(companyReports);

    var response = companyReportInPortImpl.getEmergencyReport(EmergencyReport.builder()
            .fromDate("2023-01-01").toDate("2023-01-10").build(),
        "COMP_cdc002ca-a3a0-4226-a8ff-cb27ae32262d",
        "cdh-adapter-service", "ccui");

    assertNotNull(response);
  }

}