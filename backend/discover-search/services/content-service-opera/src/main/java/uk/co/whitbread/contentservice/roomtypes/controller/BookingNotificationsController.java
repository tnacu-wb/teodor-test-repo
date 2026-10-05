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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.contentservice.roomtypes.model.BookingNotificationsResponse;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.RateCode;
import uk.co.whitbread.contentservice.roomtypes.service.BookingNotificationsService;

@RestController
@Tag(name = "Booking Notifications")
@Slf4j
@RequiredArgsConstructor
public class BookingNotificationsController {

    private final BookingNotificationsService bookingNotificationsService;

    @Operation(summary = "Gets the booking notifications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                content = @Content(schema = @Schema(implementation = BookingNotificationsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing request data.",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "country", description = "The country code to use for content retrieval.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "gb", allowableValues = "gb,de"))),
            @Parameter(in = ParameterIn.QUERY, name = "language", description = "The language code to use for content retrieval.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "en", allowableValues = "en,de"))),
            @Parameter(in = ParameterIn.QUERY, name = "brand", description = "The Brand of the hotel.",
                content = @Content(schema = @Schema(type = "string", defaultValue = "pi", allowableValues = "pi,hub,zip,pid"))),
            @Parameter(in = ParameterIn.QUERY, name = "rate", description = "The Rate code.",
                content = @Content(schema = @Schema(type = "string", allowableValues = "a,f,p,q,r,s,h,g,e")))
    })
    @RequestMapping(value = "/content/bookinginfomessages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingNotificationsResponse getBookingNotifications(@RequestParam(required = false, name = "country",  defaultValue = "gb") CountryCode country,
                                                                @RequestParam(required = false, name = "language", defaultValue = "en") LanguageCode language,
                                                                @RequestParam(required = false, name = "brand", defaultValue = "pi") BrandCode brand,
                                                                @RequestParam(required = false, name = "rate") RateCode rate) {

        log.info("Requesting booking notifications for country {}, brand {} and rate {}", country, brand.name(), rate);
        final BookingNotificationsResponse bookingNotificationsResponse = bookingNotificationsService
                .getBookingNotifications(country.name(), language.name(), brand.name(), rate.name());

        log.info("Request completed for getting booking notifications for country {}, brand {} and rate {}", country, brand.name(), rate);
        return bookingNotificationsResponse;
    }

}
