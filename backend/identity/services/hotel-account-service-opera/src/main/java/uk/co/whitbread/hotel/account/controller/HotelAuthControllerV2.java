package uk.co.whitbread.hotel.account.controller;

import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordRequest;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordResponse;
import uk.co.whitbread.hotel.account.service.HotelAuthServiceV2;

@RequestMapping("/v2/auth/hotels")
@RestController
@RequiredArgsConstructor
@Tag(name = "Hotel Authentication V2 operations")
@Slf4j
public class HotelAuthControllerV2 {

    private final HotelAuthServiceV2 hotelAuthServiceV2;

    @Operation(method = "forgotPasswordV2",
            description = "Maps the ForgotPassword operation as a resource",
            summary = "Triggers the forget password process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Good!",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ForgottenPasswordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/forgot-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ForgottenPasswordResponse forgotPassword(
            @RequestParam(defaultValue = "false", required = false) boolean business,
            @RequestBody @Valid ForgottenPasswordRequest request) {
        log.debug("Called /v2/auth/hotels/forgot-password with {} and isBusiness {}",
            sanitizeInputString(request.toString()),
            business);
        return hotelAuthServiceV2.forgotPassword(request, business);
    }
}
