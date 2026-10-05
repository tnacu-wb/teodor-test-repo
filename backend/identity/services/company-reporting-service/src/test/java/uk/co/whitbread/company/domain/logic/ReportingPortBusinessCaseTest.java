package uk.co.whitbread.company.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getManagementInformationRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import uk.co.whitbread.company.domain.model.out.EmergencyReportResult;
import uk.co.whitbread.company.domain.model.out.MiReportResult;
import uk.co.whitbread.company.domain.ports.secondary.ReportOutPort;

@ExtendWith(MockitoExtension.class)
class ReportingPortBusinessCaseTest {

  @InjectMocks
  private ReportingPortBusinessCase reportingPortBusinessCase;

  @Mock
  private ReportOutPort reportOutPort;

  @Mock
  private HttpHeaders headers;

  private static final String DATE_FORMAT = "dd-MM-yyyy";

  @Test
  void getManagementInformationReport__ShouldReturnOK() {

    //Arrange
    var request = getManagementInformationRequest();
    byte[] byteArray = new byte[]{};

    when(reportOutPort.getManagementInformationReport(request)).thenReturn(byteArray);

    //Act
    var report = reportingPortBusinessCase.getManagementInformationReport(request);

    //Assert
    assertThat(report, notNullValue());

  }

  @Test
  void getReportHeaders__ShouldReturnOK() {

    //Arrange
    var request = getManagementInformationRequest();

    when(reportOutPort.getHeaders(request)).thenReturn(headers);

    //Act
    var result = reportingPortBusinessCase.getReportHeaders(request);

    //Assert
    assertThat(result, notNullValue());

  }

  @Test
  void getManagementInformationReportUrl__ShouldReturnOK() {

    //Arrange
    var request = getManagementInformationRequest();

    when(reportOutPort.getManagementInformationReportUrl(request)).thenReturn(
        new MiReportResult("Management Information Report",
            "MI_Report_01-01-2023_30-04-2023_WithQnA.xls",
            " "));

    //Act
    final var report = reportingPortBusinessCase.getManagementInformationReportUrl(request);

    //Assert
    assertThat(report, notNullValue());
    assertThat(report.downloadUrl(),
        is(" "));
    assertThat(report.fileName(), is("MI_Report_01-01-2023_30-04-2023_WithQnA.xls"));
    assertThat(report.reportName(), is("Management Information Report"));

  }


  @Test
  void getEmergencyReportUrl__ShouldReturnOK() {

    //Arrange
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, 2);

    String startDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
    String endDate = new SimpleDateFormat(DATE_FORMAT).format(c.getTime());
    String fileName = "Emergency_Report_" + startDate + "_" + endDate + ".xls";

    when(reportOutPort.getEmergencyReportUrl(any(), any(), any(), any())).thenReturn(
        new EmergencyReportResult("Emergency Report",
            fileName,
            " "));

    //Act
    final var report = reportingPortBusinessCase.getEmergencyReportUrl(any(), any(), any(), any());

    //Assert
    assertThat(report, notNullValue());
    assertThat(report.downloadUrl(),
        is(" "));
    assertThat(report.fileName(), is(fileName));
    assertThat(report.reportName(), is("Emergency Report"));

  }

  @Test
  void validateDateRange__shouldReturnOK() {
    var request = getManagementInformationRequest();
    reportingPortBusinessCase.validateDateRange(request);
    verify(reportOutPort, times(1)).validateDateRange(request);
  }
}
