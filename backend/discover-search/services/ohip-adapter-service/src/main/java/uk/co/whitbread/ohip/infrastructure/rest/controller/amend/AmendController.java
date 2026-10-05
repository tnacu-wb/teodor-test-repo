package uk.co.whitbread.ohip.infrastructure.rest.controller.amend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.AmendInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.mapper.AmendSummaryRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.mapper.AmendSummaryResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.model.in.AmendSummaryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.model.out.AmendSummaryResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AmendController {
  private final AmendSummaryRequestMapper amendSummaryRequestMapper;
  private final AmendSummaryResponseMapper amendSummaryResponseMapper;
  private final AmendInPort amendInPort;

  @Operation(summary = "Get details from rateInfo to amend summary")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AmendSummaryResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/amend/getDetailsForAmend", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AmendSummaryResponseDto> getReservationDetailsForAmend(
      @ParameterObject @Valid AmendSummaryRequestDto amendSummaryRequestDto) {
    var amendSummaryRequest = amendSummaryRequestMapper.toModel(amendSummaryRequestDto);
    var amendSummaryResponse = amendInPort.getAmendSummary(amendSummaryRequest);
    var amendSummaryResponseDto = amendSummaryResponseMapper.toDto(amendSummaryResponse);
    return ResponseEntity.status(HttpStatus.OK).body(amendSummaryResponseDto);
  }
}
