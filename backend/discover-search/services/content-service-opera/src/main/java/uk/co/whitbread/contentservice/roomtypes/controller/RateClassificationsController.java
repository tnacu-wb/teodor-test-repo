package uk.co.whitbread.contentservice.roomtypes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.RateClassificationResponse;
import uk.co.whitbread.contentservice.roomtypes.service.RateClassificationService;

@RestController
@Tag(name = "Rate Classification")
@Slf4j
@RequiredArgsConstructor
public class RateClassificationsController {

    private final RateClassificationService rateClassificationService;


    @Operation(summary = "Gets the information for different rates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                content = @Content(schema = @Schema(implementation = RateClassificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing request data.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "hotelCode", description = "Hotel Code for specific overrides.",
                content = @Content(schema = @Schema(type = "string", defaultValue = ""))),
            @Parameter(in = ParameterIn.QUERY, name = "language", description = "The language code to use for content retrieval.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "en", allowableValues = "en,de"))),
            @Parameter(in = ParameterIn.QUERY, name = "brand", description = "The Brand of the hotel.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "pi", allowableValues = "pi,hub,zip,pid")))
    })
    @RequestMapping(value = "/content/rateclassifications",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RateClassificationResponse getAllRateClassifications(@RequestParam(required = false, name = "hotelCode", defaultValue = "") String hotelCode,
                                          @RequestParam(required = false, name = "language", defaultValue = "en") LanguageCode language,
                                          @RequestParam(required = false, name = "brand", defaultValue = "pi") BrandCode brand) {

        log.info("Requesting rate classification for brand {} and hotelCode {}", brand, hotelCode);
        final RateClassificationResponse rateClassificationResponse = rateClassificationService.getAllRateClassifications(language.name(), brand.name(), hotelCode);

        log.info("Request completed for getting rate classifications for brand {} and hotelCode {}", brand, hotelCode);
        return rateClassificationResponse;
    }


    @Operation(summary = "Gets the information for different rates.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                content = @Content(schema = @Schema(implementation = RateClassificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing request data.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "hotelCode", description = "Optional hotel code for rate override",
                content = @Content(schema = @Schema(type = "string", defaultValue = "", allowableValues = ""))),
            @Parameter(in = ParameterIn.QUERY, name = "language", description = "The language code to use for content retrieval.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "en", allowableValues = "en,de"))),
            @Parameter(in = ParameterIn.QUERY, name = "brand", description = "The Brand of the hotel.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "pi", allowableValues = "pi,hub,zip,pid")))
    })
            @RequestMapping(value = "/content/rateclassifications/{rateClassification}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RateClassificationResponse getRateClassificationForRate(@RequestParam(required = false, name = "hotelCode",  defaultValue = "") String hotelCode,
                                                 @RequestParam(required = false, name = "language", defaultValue = "en") LanguageCode language,
                                                 @RequestParam(required = false, name = "brand", defaultValue = "pi") BrandCode brand,
                                                 @PathVariable(value = "rateClassification") String rateClassification) {

        log.info("Requesting rate classification - {} for brand {} and hotelCode {}", rateClassification, brand.name(), hotelCode);
        final RateClassificationResponse rateClassificationResponse = rateClassificationService.getRateClassificationsForRate(language.name(), brand.name(), hotelCode, rateClassification);

        log.info("Request completed for getting rate classification {} for brand {} and hotelCode {}", rateClassification, brand.name(), hotelCode);
        return rateClassificationResponse;
    }

}
