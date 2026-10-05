package uk.co.whitbread.address.lookup.infrastructure.rest.controller.address;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.in.AddressSearchRequestDto;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressFormatResponseDto;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressSearchResponseDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;


public interface AddressLookupApiDocumentation {

  String ADDRESS_LOOKUP_BY_POSTCODE_PATH = "/addresses";
  String FORMAT_ADDRESS_PATH = "/addresses/{monikerId}";

  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = ADDRESS_LOOKUP_BY_POSTCODE_PATH,
      operation = @Operation(operationId = "getAddressByPostcode",
          summary = "Get address by postcode",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "searchTerm", example = "SG89ES",
                  description = "searchTerm", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "countryCode", example = "uk", description = "countryCode")
          }))
  })
  @ApiResponse(responseCode = "200", description = "Address lookup successfully retrieved",
      content = @Content(schema = @Schema(implementation = AddressSearchResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "Missing or invalid request",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "404", description = "Resource Not Found",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  ResponseEntity<List<AddressSearchResponseDto>> getAddressesByPostcode(
      @ParameterObject AddressSearchRequestDto addressSearchRequestDto);


  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = FORMAT_ADDRESS_PATH,
      operation = @Operation(operationId = "getFormattedAddress",
          summary = "Format address by moniker-id",
          parameters = {
              @Parameter(in = ParameterIn.PATH, name = "monikerId", description = "moniker id", required = true)
          }))
  })
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(schema = @Schema(implementation = AddressFormatResponseDto.class)))
  @ApiResponse(responseCode = "400", description = "Missing or invalid request",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "404", description = "Resource Not Found",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  ResponseEntity<AddressFormatResponseDto> getFormattedAddress(String monikerId);

}
