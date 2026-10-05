package uk.co.whitbread.cdh.infrastructure.rest.controller.report;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;
import uk.co.whitbread.cdh.domain.ports.primary.CompanyReportInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper.CompanyReportsDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper.EmergencyReportDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper.EmergencyResultsDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper.ManagementInformationDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.EmergencyReportRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.ManagementInformationRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.EmergencyReportResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.ManagementInformationResponseDto;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

  @InjectMocks
  private ReportController controller;


  @Mock
  ManagementInformationDtoMapper managementInformationDtoMapper;

  @Mock
  EmergencyReportDtoMapper emergencyReportDtoMapper;
  @Mock
  CompanyReportInPort companyReportInPort;
  @Mock
  CompanyReportsDtoMapper companyReportsDtoMapper;
  @Mock
  EmergencyResultsDtoMapper emergencyResultsDtoMapper;

  @Test
  void test_getMiReport() {
    ManagementInformationRequestDto managementInformationRequestDto = ManagementInformationRequestDto
        .builder()
        .accessContext("test")
        .accessedBy("junit")
        .fromDate("2023-01-01")
        .toDate("2023-01-15")
        .build();

    ManagementInformation managementInformation = ManagementInformation
        .builder()
        .fromDate("2023-01-01")
        .toDate("2023-01-15")
        .build();

    when(managementInformationDtoMapper.toModel(managementInformationRequestDto))
        .thenReturn(managementInformation);
    when(companyReportInPort.getManagementInformation(managementInformation, "test-id", "junit",
        "test"))
        .thenReturn(CompanyReports.builder().build());
    when(companyReportsDtoMapper.toDto(CompanyReports.builder().build()))
        .thenReturn(ManagementInformationResponseDto.builder().build());

    var response = controller.getManagementInformation("test-id", managementInformationRequestDto);
    assertNotNull(response);

  }

  @Test
  void test_getEmergencyReport() {
    EmergencyReportRequestDto emergencyReportRequestDto = EmergencyReportRequestDto
        .builder()
        .accessContext("test")
        .accessedBy("junit")
        .fromDate("2023-01-01")
        .toDate("2023-01-15")
        .build();

    EmergencyReport emergencyReport = EmergencyReport
        .builder()
        .fromDate("2023-01-01")
        .toDate("2023-01-15")
        .build();

    when(emergencyReportDtoMapper.toModel(emergencyReportRequestDto))
        .thenReturn(emergencyReport);
    when(companyReportInPort.getEmergencyReport(emergencyReport, "test-id", "junit",
        "test"))
        .thenReturn(EmergencyReportResults.builder().build());
    when(emergencyResultsDtoMapper.toDto(EmergencyReportResults.builder().build()))
        .thenReturn(EmergencyReportResponseDto.builder().build());

    var response = controller.getEmergencyReport("test-id", emergencyReportRequestDto);
    assertNotNull(response);

  }

}
