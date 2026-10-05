package uk.co.whitbread.content.infrastructure.rest.controller.seo;

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
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.in.SeoRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.out.SeoResponseDto;

public interface SeoApiDocumentation {

  String SEO_INFO_PATH = "v1/content/seo";

  @Operation(summary = "Retrieves SEO Info")
  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = SEO_INFO_PATH,
      operation = @Operation(summary = "Retrieves SEO Information",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "hotelId", example = "MANOLD",
                  description = "Hotel ID to retrieve SEO information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "page", example = "MANOLD",
                  schema = @Schema(allowableValues = {"Home", "SRP", "HDP", "Ancillaries",
                      "GuestDetails", "Payment", "Confirmation", "Amend", "Dashboard"}),
                  description = "Page to retrieve SEO information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb",
                  description = "Country to retrieve SEO information", required = true),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en",
                  description = "Language to retrieve SEO information", required = true)
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Success",
                  content = @Content(mediaType = "application/json",
                      schema = @Schema(implementation = SeoResponseDto.class))),
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
  ResponseEntity<SeoResponseDto> getSeo(@Valid @ParameterObject SeoRequestDto seoRequestDto);
}
