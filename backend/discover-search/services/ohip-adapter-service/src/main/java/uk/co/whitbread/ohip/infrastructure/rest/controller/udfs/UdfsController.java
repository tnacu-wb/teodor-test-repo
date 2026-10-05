package uk.co.whitbread.ohip.infrastructure.rest.controller.udfs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.UdfsInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.udfs.mapper.UdfsDomainMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.udfs.model.in.UdfsRequestDto;

@RestController
@RequiredArgsConstructor
public class UdfsController {

  private final UdfsInPort udfsInPort;
  private final UdfsDomainMapper udfsMapper;

  @Operation(summary = "Update the reservation in Opera with the character UDFs. "
      + "The updated UDFs are available through the /v1/reservations/basket API.")
  @ApiResponse(responseCode = "204", description = "Success")
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(
      value = "/reservations/characterudfs", produces = MediaType.APPLICATION_JSON_VALUE)
  public void updateCharacterUdfs(@Valid @RequestBody UdfsRequestDto udfsDto) {
    udfsInPort.updateUdfs(udfsMapper.toDomainModel(udfsDto));
  }
}