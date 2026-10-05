package uk.co.whitbread.cdh.infrastructure.rest.controller.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.EmergencyReportRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.ManagementInformationRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.EmergencyReportResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.ManagementInformationResponseDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;


public interface ReportControllerApiDocumentation {

  @Operation(summary = "Management Information from CDH Booking Services API V2")
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ManagementInformationResponseDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Unauthorized",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  ManagementInformationResponseDto getManagementInformation(
      @PathVariable("companyId") @NotNull String companyId,
      @Valid @ParameterObject ManagementInformationRequestDto managementInformationRequestDto);

  @Operation(summary = "Emergency Report from CDH Booking Services API V2")
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = EmergencyReportResponseDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Unauthorized",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  EmergencyReportResponseDto getEmergencyReport(
      @PathVariable("companyId") @NotNull String companyId,
      @Valid @ParameterObject EmergencyReportRequestDto managementInformationRequestDto);

}
