package uk.co.whitbread.hotel.account.controller;

import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.account.client.HotelAccountCacheProvider;
import uk.co.whitbread.hotel.account.model.BookingChannelCode;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.HotelBrandCode;
import uk.co.whitbread.hotel.account.model.HotelCustomerRequest;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customers/internal")
@Tag(name = "Hotel Customer operations Cached for internal use only")
@Validated
public class HotelAccountsCacheController {

    private static final String DEFAULT_PI_BOOKING_CHANNEL = "WEB";

    private static final String DEFAULT_BRAND = "PI";

    private final HotelAccountCacheProvider hotelAccountCacheProvider;
    private final TokenService authTokenService;

    @Operation(method = "getCachedCustomer", description = "Get Customer",
            summary = "This endpoint retrieves the details of an existing customer resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request: check you send customer-id and session-id",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized Error: Provided token is invalid or has expired",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found: account not found for given customer-id and session-id",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/{customer-id}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Customer getCustomer(
            @PathVariable("customer-id") String customerId,
            @RequestHeader(name = "hotel-brand", required = false, defaultValue = DEFAULT_BRAND) HotelBrandCode hotelBrand,
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader(required = false, defaultValue = DEFAULT_PI_BOOKING_CHANNEL) BookingChannelCode bookingChannel,
            @RequestParam(required = false, defaultValue = "false") boolean business) {

        log.debug("Called /customers/internal/{} (GET) (b:{})",
            sanitizeInputString(customerId), business);

        return hotelAccountCacheProvider.getCustomer(
                HotelCustomerRequest.builder().authorization(authorization).customerId(customerId)
                    .hotelBrand(hotelBrand).business(business).bookingChannel(bookingChannel)
                    .build());
    }

}
