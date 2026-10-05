package uk.co.whitbread.company.infrastructure.rest.controller.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.company.domain.ports.primary.ReportInPort;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;
import uk.co.whitbread.company.infrastructure.rest.controller.company.mapper.EmergencyReportResponseDtoMapper;
import uk.co.whitbread.company.infrastructure.rest.controller.company.mapper.ManagementInformationRequestDtoMapper;
import uk.co.whitbread.company.infrastructure.rest.controller.company.mapper.MiReportResponseDtoMapper;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.EmergencyReportRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.ManagementInformationRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.EmergencyReportResponseDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.MiReportResponseDto;

@RequestMapping("/v1")
@RestController
@Slf4j
@RequiredArgsConstructor
public class ReportingController {

  private final ManagementInformationRequestDtoMapper managementInformationRequestDtoMapper;

  private final ReportInPort reportInPort;
  private final CdhAdapterProperties cdhAdapterProperties;
  private final MiReportResponseDtoMapper miReportResponseDtoMapper;
  private final EmergencyReportResponseDtoMapper emergencyReportResponseDtoMapper;

  private static final String WB_AUTHORIZATION = "WB-Authorization";
  private static final String COMPANY_ID = "companyId";

  @Operation(summary = "Creates management information report")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
          schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/company-reports/admin/{companyId}/management-information-report",
      produces = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<byte[]> getManagementInformationReport(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @PathVariable("companyId") @NotNull String companyId,
      @Valid @ParameterObject ManagementInformationRequestDto managementInformationRequestDto) {

    var request = managementInformationRequestDtoMapper.toDomainModel(companyId,
        cdhAdapterProperties.getAccessContext(), cdhAdapterProperties.getAccessedBy(),
        managementInformationRequestDto);
    reportInPort.validateDateRange(request);
    var byteArray = reportInPort.getManagementInformationReport(request);
    var headers = reportInPort.getReportHeaders(request);
    return new ResponseEntity<>(byteArray, headers, HttpStatus.OK);
  }

  @Operation(summary = "Creates management information report")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MiReportResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/company-reports/admin/management-information-report",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public MiReportResponseDto getManagementInformationS3Report(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @Valid @ParameterObject ManagementInformationRequestDto managementInformationRequestDto,
      @NotNull HttpServletRequest httpServletRequest) {

    String companyId = (String) httpServletRequest.getAttribute(COMPANY_ID);
    var request = managementInformationRequestDtoMapper.toDomainModel(
        companyId,
        cdhAdapterProperties.getAccessContext(), cdhAdapterProperties.getAccessedBy(),
        managementInformationRequestDto);
    reportInPort.validateDateRange(request);
    final var managementInformationReportResult = reportInPort.getManagementInformationReportUrl(
        request);
    return miReportResponseDtoMapper.toResponseDto(managementInformationReportResult);
  }


  @Operation(summary = "Creates emergency report")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MiReportResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/company-reports/admin/emergency-report",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public EmergencyReportResponseDto getEmergencyReport(
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @NotNull HttpServletRequest httpServletRequest,
      @Valid @ParameterObject EmergencyReportRequestDto emergencyReportRequestDto) {

    String companyId = (String) httpServletRequest.getAttribute(COMPANY_ID);

    final var emergencyReportResult = reportInPort.getEmergencyReportUrl(
        companyId,
        cdhAdapterProperties.getAccessContext(), cdhAdapterProperties.getAccessedBy(),
        emergencyReportRequestDto.language());
    return emergencyReportResponseDtoMapper.toResponseDto(emergencyReportResult);
  }

}
