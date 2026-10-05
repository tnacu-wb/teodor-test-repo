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
import uk.co.whitbread.contentservice.roomtypes.model.*;
import uk.co.whitbread.contentservice.roomtypes.service.CookiePoliciesService;

@RestController
@Tag(name = "Cookie Policies")
@Slf4j
@RequiredArgsConstructor
public class CookiePoliciesController {

    private final CookiePoliciesService cookiePoliciesService;

    @Operation(summary = "Gets cookie policies based on language, brand, and subBrand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                content = @Content(schema = @Schema(implementation = CookiePoliciesResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing request data.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "language", description = "The language code to use for content retrieval.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "en", allowableValues = "en,de"))),
            @Parameter(in = ParameterIn.QUERY, name = "brand", description = "The Brand of the privacy policies.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "pi", allowableValues = "pi,restaurant,businessbooker"))),
            @Parameter(in = ParameterIn.QUERY, name = "subBrand", description = "The subBrand of a brand.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "none",
                    allowableValues = "none,beefeater,tabletable,brewersfayre,cookhouseandpub,barandblock,whitbreadinns")))
    })
    @RequestMapping(value = "/content/cookiepolicies",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CookiePoliciesResponse getCookiePolicies(@RequestParam(required = false, name = "language", defaultValue = "en") LanguageCode language,
                                                    @RequestParam(required = false, name = "brand", defaultValue = "pi") BrandCode brand,
                                                    @RequestParam(required = false, name = "subBrand", defaultValue = "none") SubBrandCode subBrand) {
        log.info("Requesting cookie policies for language {} brand {} and subBrand {}", language, brand, subBrand);
        final CookiePoliciesResponse cookiePoliciesResponse = cookiePoliciesService.getCookiePolicies(language.name(), brand.name(), subBrand.name());

        log.info("Request completed for getting policies for language {} brand {} and subBrand {}", language, brand, subBrand);
        return cookiePoliciesResponse;
    }
}
