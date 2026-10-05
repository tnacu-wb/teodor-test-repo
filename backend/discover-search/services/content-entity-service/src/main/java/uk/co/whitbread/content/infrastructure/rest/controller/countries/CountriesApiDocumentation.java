package uk.co.whitbread.content.infrastructure.rest.controller.countries;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.in.CountriesRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.countries.model.out.CountriesDto;

public interface CountriesApiDocumentation {

  String COUNTRIES_INFO_PATH = "/v1/content/countries";

  @Operation(summary = "Retrieves Countries Info")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = COUNTRIES_INFO_PATH,
      operation = @Operation(summary = "Retrieves Countries Information",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve countries information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve countries information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "site", example = "leisure",
                  description = "Site to retrieve countries information", required = true)
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json",
                      schema = @Schema(implementation = Map.class))}),
              @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
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
  ResponseEntity<CountriesDto> getCountries(
      @Valid @ParameterObject CountriesRequestDto countriesRequestDto);
}
