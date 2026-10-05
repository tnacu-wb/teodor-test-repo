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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.RateClassificationResponse;
import uk.co.whitbread.contentservice.roomtypes.model.RoomTypesResponse;
import uk.co.whitbread.contentservice.roomtypes.service.RoomTypesService;

@RestController
@Tag(name = "Room Types")
@Slf4j
@RequiredArgsConstructor
public class RoomTypesController {

    private final RoomTypesService roomTypesService;

    @Operation(summary = "Gets the information for different room types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                content = @Content(schema = @Schema(implementation = RoomTypesResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing request data.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "country", description = "The country code to use for content retrieval.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "gb", allowableValues = "gb,de"))),
            @Parameter(in = ParameterIn.QUERY, name = "language", description = "The language code to use for content retrieval.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "en", allowableValues = "en,de"))),
            @Parameter(in = ParameterIn.QUERY, name = "brand", description = "The Brand of the hotel.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "pi", allowableValues = "pi,hub,zip,pid")))
    })
    @RequestMapping(value = "/content/roomtypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RoomTypesResponse getRoomTypes(@RequestParam(required = false, name = "country", defaultValue = "gb") CountryCode country,
                                          @RequestParam(required = false, name = "language", defaultValue = "en") LanguageCode language,
                                          @RequestParam(required = false, name = "brand", defaultValue = "pi") BrandCode brand) {

        log.info("Requesting room types for brand {}", brand);
        final RoomTypesResponse roomTypesResponse = roomTypesService.getAllRoomTypes(country.name(), language.name(), brand.name());

        log.info("Request completed for getting room types for brand {}", brand);
        return roomTypesResponse;
    }


    @Operation(summary = "Gets the information for different room types.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(schema = @Schema(implementation = RoomTypesResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid or missing request data.",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Parameters({
        @Parameter(in = ParameterIn.QUERY, name = "country", description = "The country code to use for content retrieval.",
            content = @Content(schema = @Schema(type = "string", defaultValue = "gb", allowableValues = "gb,de"))),
        @Parameter(in = ParameterIn.QUERY, name = "language", description = "The language code to use for content retrieval.",
            content = @Content(schema = @Schema(type = "string", defaultValue = "en", allowableValues = "en,de"))),
        @Parameter(in = ParameterIn.QUERY, name = "brand", description = "The Brand of the hotel.",
            content = @Content(schema = @Schema(type = "string", defaultValue = "pi", allowableValues = "pi,hub,zip,pid")))
    })
            @RequestMapping(value = "/content/roomtypes/{roomTypeCode}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RoomTypesResponse getRoomTypesForCode(@RequestParam(required = false, name = "country",  defaultValue = "gb") CountryCode country,
                                                 @RequestParam(required = false, name = "language", defaultValue = "en") LanguageCode language,
                                                 @RequestParam(required = false, name = "brand", defaultValue = "pi") BrandCode brand,
                                                 @PathVariable(value = "roomTypeCode") String roomTypeCode) {

        log.info("Requesting room type - {} for brand {}", roomTypeCode, brand.name());
        final RoomTypesResponse roomTypesResponse = roomTypesService.getRoomTypesForCode(country.name(), language.name(), brand.name(), roomTypeCode);

        log.info("Request completed for getting room type {} for brand {}", roomTypeCode, brand.name());
        return roomTypesResponse;
    }

}