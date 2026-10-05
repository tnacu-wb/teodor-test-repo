package uk.co.whitbread.content.infrastructure.rest.controller.roomtype;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.in.RoomTypeRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.out.RoomTypeDto;

public interface RoomTypeApiDocumentation {

  String ROOM_TYPE_PATH = "/v1/content";

  @Operation(summary = "Retrieves Room Types ")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = ROOM_TYPE_PATH + "/roomType",
      operation = @Operation(summary = "Retrieves Room Type Information",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve room type", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve room type ", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "brand", example = "pi",
                  description = "Brand to retrieve room type", required = true),
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success",
                  content = @Content(mediaType = "application/json",
                      schema = @Schema(implementation = RoomTypeDto.class))),
              @ApiResponse(responseCode = "400", description = "Error Occurred",
                  content = {
                      @Content(mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class))}),
              @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponse.class))}),
              @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponse.class))})
          }))
  })
  ResponseEntity<RoomTypeDto> getRoomTypeInformation(
      @Valid @ParameterObject RoomTypeRequestDto roomTypeRequestDto);
}
