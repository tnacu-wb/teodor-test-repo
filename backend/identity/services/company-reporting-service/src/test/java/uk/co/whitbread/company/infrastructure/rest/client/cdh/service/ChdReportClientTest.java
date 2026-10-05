package uk.co.whitbread.company.infrastructure.rest.client.cdh.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.EMERGENCY_REPORT_COLUMNS_EN;
import static uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient.MANAGEMENT_INFORMATION_REPORT_COLUMNS_EN;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getCdhEmergencyReportRequest;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getManagementInformationRequest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient;

@ExtendWith(MockitoExtension.class)
class ChdReportClientTest {

  @InjectMocks
  private CdhReportClient cdhReportClient;

  @Test
  void retrieveSpreadsheetReturnsExpectedResultWhenValidInputsProvided() {
    List<List<String>> reports = new ArrayList<>();
    reports.add(Arrays.asList("Booking Reference 1", "Booking Status 1", "Total Cost (inc VAT) 1"));
    reports.add(Arrays.asList("Booking Reference 2", "Booking Status 2", "Total Cost (inc VAT) 2"));

    var request = getManagementInformationRequest();

    ByteArrayOutputStream result = cdhReportClient.retrieveSpreadsheet(reports,
        MANAGEMENT_INFORMATION_REPORT_COLUMNS_EN, request);

    assertThat(result, notNullValue());
  }

  @Test
  void retrieveSpreadsheetThrowsExceptionWhenIOExceptionOccurs() {

    var request = getManagementInformationRequest();
    assertThrows(NullPointerException.class,
        () -> cdhReportClient.retrieveSpreadsheet(null, null,
            null));
  }

  @Test
  void retrieveEmergencySpreadsheetReturnsExpectedResultWhenValidInputsProvided() {
    List<List<String>> reports = new ArrayList<>();
    reports.add(Arrays.asList("Booking Reference 1", "Booking ID 1", "Hotel Area 1"));
    reports.add(Arrays.asList("Booking Reference 2", "Booking ID 2", "Hotel Area 2"));

    var request = getCdhEmergencyReportRequest();

    ByteArrayOutputStream result = cdhReportClient.retrieveEmergencySpreadsheet(reports,
        EMERGENCY_REPORT_COLUMNS_EN, request);

    assertThat(result, notNullValue());

  }
}
