package uk.co.whitbread.company.infrastructure.rest.client.cdh.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.COMPANY_ID;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getCdhEmergencyReportRequest;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getCdhMiReportRequestDto;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getCdhMiReportResponseDto;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getEmergencyReportTestData;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.EmergencyReportResponseDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.ManagementInformationResponseDto;
import uk.co.whitbread.company.domain.model.out.EmergencyReportResult;

@ExtendWith(MockitoExtension.class)
class CdhAdapterClientTest {

  @Mock
  private RestClient restClient;
  @Mock
  private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private RestClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private RestClient.ResponseSpec responseSpec;

  @InjectMocks
  private CdhAdapterClient cdhAdapterClient;


  @Test
  void getManagementInformation__ShouldReturnOK() throws IOException {

    var cdhMiReportRequestDto = getCdhMiReportRequestDto();

    when(restClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(ErrorHandler.class))).thenReturn(
        responseSpec);
    when(responseSpec.body(ManagementInformationResponseDto.class)).thenReturn(
        getCdhMiReportResponseDto());

    var response = cdhAdapterClient.getManagementInformation(COMPANY_ID, cdhMiReportRequestDto);

    assertThat(response, notNullValue());

  }

  @Test
  void getEmergencyReport_ShouldReturnOK() throws IOException {

    var cdhEmergencyReportRequest = getCdhEmergencyReportRequest();
    var cdhEmergencyReportResponseDto = getEmergencyReportTestData();

    when(restClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(ErrorHandler.class))).thenReturn(
        responseSpec);
    when(responseSpec.body(EmergencyReportResponseDto.class)).thenReturn(
        getEmergencyReportTestData());

    var response = cdhAdapterClient.getEmergencyReport(COMPANY_ID, cdhEmergencyReportRequest);

    assertThat(response, notNullValue());

  }

  @Test
  void getEmergencyReport_ShouldThrowNullPointerException() {
    var cdhEmergencyReportRequest = getCdhEmergencyReportRequest();

    when(restClient.get()).thenThrow(new NullPointerException());

    assertThrows(NullPointerException.class, () -> {
      cdhAdapterClient.getEmergencyReport(COMPANY_ID, cdhEmergencyReportRequest);
    });
  }

  @Test
  void getEmergencyReport_ShouldThrowArrayIndexOutOfBoundsException() {
    var cdhEmergencyReportRequest = getCdhEmergencyReportRequest();

    when(restClient.get()).thenThrow(new ArrayIndexOutOfBoundsException());

    assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
      cdhAdapterClient.getEmergencyReport(COMPANY_ID, cdhEmergencyReportRequest);
    });
  }

}
