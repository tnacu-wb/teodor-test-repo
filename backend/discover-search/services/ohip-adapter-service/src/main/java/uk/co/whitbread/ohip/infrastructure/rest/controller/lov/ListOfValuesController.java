package uk.co.whitbread.ohip.infrastructure.rest.controller.lov;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.ListOfValuesInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.lov.mapper.CancellationReasonsDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.lov.model.out.CancellationReasonsResponseDto;

@RestController
@RequiredArgsConstructor
public class ListOfValuesController {

  private final ListOfValuesInPort listOfValuesInPort;
  private final CancellationReasonsDtoMapper cancellationReasonsDtoMapper;

  @Operation(summary = "Get list of cancellation reasons.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CancellationReasonsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/hotels/{hotelId}/cancellationReasons", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CancellationReasonsResponseDto> getCancellationReasons(
      @PathVariable("hotelId") String hotelId) {

    final var cancellationReasonsResponse = listOfValuesInPort.getListOfCancellationReasons(
        hotelId);
    var cancellationReasonsResponseDto = cancellationReasonsDtoMapper
        .toDto(cancellationReasonsResponse);

    return ResponseEntity.status(HttpStatus.OK).body(cancellationReasonsResponseDto);
  }

}
