package uk.co.whitbread.cdh.infrastructure.rest.controller.report;

import static uk.co.whitbread.cdh.infrastructure.util.Utils.sanitizeInputString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.cdh.domain.ports.primary.CompanyReportInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper.CompanyReportsDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper.EmergencyReportDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper.EmergencyResultsDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper.ManagementInformationDtoMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.EmergencyReportRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.ManagementInformationRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.EmergencyReportResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.ManagementInformationResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/cdh")
public class ReportController implements ReportControllerApiDocumentation {

  private final ManagementInformationDtoMapper managementInformationDtoMapper;
  private final EmergencyReportDtoMapper emergencyReportDtoMapper;
  private final CompanyReportInPort companyReportInPort;
  private final CompanyReportsDtoMapper companyReportsDtoMapper;
  private final EmergencyResultsDtoMapper emergencyResultsDtoMapper;

  @GetMapping(value = "/companies/{companyId}/reports/management-information",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ManagementInformationResponseDto getManagementInformation(
      @PathVariable("companyId") @NotNull String companyId,
      @ParameterObject @Valid ManagementInformationRequestDto managementInformationRequestDto) {

    log.info(
        "Request to get management information details for companyId={}, fromDate={}, toDate={}, "
            + "accessedBy={}, accessContext={}",
        sanitizeInputString(companyId),
        sanitizeInputString(managementInformationRequestDto.getFromDate()),
        sanitizeInputString(managementInformationRequestDto.getToDate()),
        sanitizeInputString(managementInformationRequestDto.getAccessedBy()),
        sanitizeInputString(managementInformationRequestDto.getAccessContext()));

    var managementInformation = managementInformationDtoMapper.toModel(
        managementInformationRequestDto);

    var managementInformationResponse = companyReportInPort.getManagementInformation(
        managementInformation, companyId, managementInformationRequestDto.getAccessedBy(),
        managementInformationRequestDto.getAccessContext());

    return companyReportsDtoMapper.toDto(managementInformationResponse);
  }

  @GetMapping(value = "/companies/{companyId}/reports/emergency-report",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public EmergencyReportResponseDto getEmergencyReport(
      @PathVariable("companyId") @NotNull String companyId,
      @ParameterObject @Valid EmergencyReportRequestDto emergencyReportRequestDto) {

    log.info(
        "Request to get emergency report for companyId={}, fromDate={}, toDate={}, accessedBy={}, accessContext={}",
        sanitizeInputString(companyId),
        sanitizeInputString(emergencyReportRequestDto.getFromDate()),
        sanitizeInputString(emergencyReportRequestDto.getToDate()),
        sanitizeInputString(emergencyReportRequestDto.getAccessedBy()),
        sanitizeInputString(emergencyReportRequestDto.getAccessContext()));

    var emergencyReport = emergencyReportDtoMapper.toModel(
        emergencyReportRequestDto);

    var emergencyReportResponse = companyReportInPort.getEmergencyReport(
        emergencyReport, companyId, emergencyReportRequestDto.getAccessedBy(),
        emergencyReportRequestDto.getAccessContext());

    return emergencyResultsDtoMapper.toDto(emergencyReportResponse);
  }
}
