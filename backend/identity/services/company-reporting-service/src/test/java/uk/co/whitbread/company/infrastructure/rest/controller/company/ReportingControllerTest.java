package uk.co.whitbread.company.infrastructure.rest.controller.company;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.ACCESSED_BY;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.ACCESS_CONTEXT;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.COMPANY_ID;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.EN;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.FROM_DATE;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.TO_DATE;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.getManagementInformationRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.company.domain.model.out.EmergencyReportResult;
import uk.co.whitbread.company.domain.ports.primary.ReportInPort;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;
import uk.co.whitbread.company.infrastructure.rest.controller.company.mapper.EmergencyReportResponseDtoMapper;
import uk.co.whitbread.company.infrastructure.rest.controller.company.mapper.ManagementInformationRequestDtoMapper;
import uk.co.whitbread.company.infrastructure.rest.controller.company.mapper.MiReportResponseDtoMapper;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.EmergencyReportRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.ManagementInformationRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.EmergencyReportResponseDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.MiReportResponseDto;


@ExtendWith(MockitoExtension.class)
class ReportingControllerTest {
  @InjectMocks
  private ReportingController managementInformationControllerUnderTest;
  @Mock
  private ManagementInformationRequestDtoMapper managementInformationRequestDtoMapper;
  @Mock
  private ReportInPort reportInPort;
  @Mock
  private CdhAdapterProperties cdhAdapterProperties;

  @Mock
  private MiReportResponseDtoMapper miReportResponseDtoMapper;

  @Mock
  private EmergencyReportResponseDtoMapper emergencyReportResponseDtoMapper;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Test
  void getManagementInformationReport__ShouldReturnOK() {
    //Arrange
    var managementInformationRequestDto = getManagementInformationRequestDto();
    var managementInformationRequest = getManagementInformationRequest();
    when(cdhAdapterProperties.getAccessContext()).thenReturn(ACCESS_CONTEXT);
    when(cdhAdapterProperties.getAccessedBy()).thenReturn(ACCESSED_BY);
    when(managementInformationRequestDtoMapper.toDomainModel(COMPANY_ID, ACCESS_CONTEXT,
        ACCESSED_BY, managementInformationRequestDto))
        .thenReturn(managementInformationRequest);

    //act
    final ResponseEntity<byte[]> response = managementInformationControllerUnderTest.getManagementInformationReport("YAbkjnasyfwenosxg",
        COMPANY_ID,
        managementInformationRequestDto);

    //Assert
    verify(reportInPort, times(1)).getReportHeaders(managementInformationRequest);
    verify(reportInPort, times(1)).getManagementInformationReport(managementInformationRequest);
    verify(reportInPort, times(1)).validateDateRange(managementInformationRequest);
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  private ManagementInformationRequestDto getManagementInformationRequestDto() {
    return new ManagementInformationRequestDto(FROM_DATE, TO_DATE, true, EN) ;
  }

  @Test
  void getManagementInformationReportUrl__ShouldReturnOK() {
    //Arrange
    httpServletRequest.setAttribute("companyId", COMPANY_ID);
    var managementInformationRequestDto = getManagementInformationRequestDto();
    var managementInformationRequest = getManagementInformationRequest();
    when(cdhAdapterProperties.getAccessContext()).thenReturn(ACCESS_CONTEXT);
    when(cdhAdapterProperties.getAccessedBy()).thenReturn(ACCESSED_BY);
    when(httpServletRequest.getAttribute(any())).thenReturn(COMPANY_ID);
    when(managementInformationRequestDtoMapper.toDomainModel(COMPANY_ID, ACCESS_CONTEXT,
        ACCESSED_BY, managementInformationRequestDto))
        .thenReturn(managementInformationRequest);

    //act
    final MiReportResponseDto response = managementInformationControllerUnderTest
        .getManagementInformationS3Report("eynmduidhhwi",
            managementInformationRequestDto, httpServletRequest);

    //Assert
    verify(reportInPort, times(1)).getManagementInformationReportUrl(managementInformationRequest);
    verify(reportInPort, times(1)).validateDateRange(managementInformationRequest);
  }

  @Test
  void getManagementInformationReportUrlReturnsExpectedResponse() {
    String authorization = "auth";
    var managementInformationRequestDto = getManagementInformationRequestDto();
    var managementInformationRequest = getManagementInformationRequest();

    when(String.valueOf(httpServletRequest.getAttribute(any()))).thenReturn("1234");
    when(cdhAdapterProperties.getAccessContext()).thenReturn("context");
    when(cdhAdapterProperties.getAccessedBy()).thenReturn("accessedBy");
    MiReportResponseDto miReportResponseDto = new MiReportResponseDto("MIReport","Mi_report_","https://testurl.com");
    when(managementInformationRequestDtoMapper.toDomainModel(any(), any(), any(), any())).thenReturn(managementInformationRequest);
    when(miReportResponseDtoMapper.toResponseDto(any())).thenReturn(miReportResponseDto);

    MiReportResponseDto response = managementInformationControllerUnderTest.getManagementInformationS3Report(authorization, managementInformationRequestDto, httpServletRequest);

    assertNotNull(response.reportName(), "Mi_report_");
  }

  @Test
  void getEmergencyReportUrlReturnsExpectedResponse() {
    String authorization = "auth";
    EmergencyReportRequestDto emergencyReportRequestDto = new EmergencyReportRequestDto("en");

    when(String.valueOf(httpServletRequest.getAttribute(any()))).thenReturn("1234");
    when(cdhAdapterProperties.getAccessContext()).thenReturn("context");
    when(cdhAdapterProperties.getAccessedBy()).thenReturn("accessedBy");
    EmergencyReportResult emergencyReportResult = new EmergencyReportResult("Emergency","Emergency_report_","https://testurl.com");
    EmergencyReportResponseDto emergencyReportResponseDto = new EmergencyReportResponseDto("Emergency","Emergency_report_","https://testurl.com");
    when(reportInPort.getEmergencyReportUrl(any(), any(), any(), any() )).thenReturn(emergencyReportResult);
    when(emergencyReportResponseDtoMapper.toResponseDto(emergencyReportResult)).thenReturn(emergencyReportResponseDto);
    EmergencyReportResponseDto response = managementInformationControllerUnderTest.getEmergencyReport(authorization,
        httpServletRequest, emergencyReportRequestDto);

    assertNotNull(response.reportName(), "Emergency_report_");
  }

}


