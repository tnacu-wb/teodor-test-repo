package uk.co.whitbread.hotel.account.controller;

import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.account.model.ChangePasswordResponse;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordRequest;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordResponse;
import uk.co.whitbread.hotel.account.model.ResetPasswordRequest;
import uk.co.whitbread.hotel.account.model.ValidateResetKeyRequest;
import uk.co.whitbread.hotel.account.model.ValidateResetKeyResponse;
import uk.co.whitbread.hotel.account.service.HotelAuthServiceCdh;

@RequestMapping("/auth/hotels")
@RestController
@RequiredArgsConstructor
@Tag(name = "Hotel Authentication operations")
@Slf4j
public class HotelAuthController {

    private final HotelAuthServiceCdh hotelAuthServiceCdh;

    @Operation(method = "forgotPassword",
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
        @Parameter @RequestHeader(name = "country", required = false, defaultValue = "gb") String countryCode,
        @Parameter @RequestHeader(name = "language", required = false, defaultValue = "en") String languageCode,
        @RequestParam(defaultValue = "false", required = false) boolean business,
        @RequestParam(defaultValue = "false", required = false) boolean innBusiness,
        @RequestBody @Valid ForgottenPasswordRequest request) {
        log.debug("Called /hotel/account/forgot-password with {} and isBusiness {}", sanitizeInputString(request.toString()), business);
        if (business || innBusiness) {
            return hotelAuthServiceCdh.forgottenPasswordTokenBusiness(request, languageCode);
        } else {
            return hotelAuthServiceCdh.forgottenPasswordToken(request, languageCode);
        }
    }

    @Operation(method = "resetPassword", description = "Resets password for non logged in users",
            summary = "This endpoint provides the functionality to update the password for an existing customer resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Good!",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChangePasswordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(value = "/forgot-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ChangePasswordResponse resetPassword(
        @RequestHeader(name = "password-token") String passwordToken,
        @Parameter @RequestHeader(name = "country", required = false, defaultValue = "gb") String countryCode,
        @Parameter @RequestHeader(name = "language", required = false, defaultValue = "en") String languageCode,
        @RequestParam(defaultValue = "false", required = false) boolean business,
        @RequestBody @Valid ResetPasswordRequest payload) {
        log.debug("Called resetPassword with isBusiness {}", business);
        if (business) {
            return hotelAuthServiceCdh.resetPasswordBusiness(payload.getCustomerId(),
                    payload.getNewPassword(), passwordToken);
        } else {
            return hotelAuthServiceCdh.resetPassword(payload.getCustomerId(),
                    payload.getNewPassword(), passwordToken);
        }
    }

    @Operation(method = "validateResetKey",
        description = "Maps the ValidateResetKey operation as a resource",
        summary = "Validates the password reset key")
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
    @PostMapping(value = "/validate-reset-key",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidateResetKeyResponse validateResetKey(@RequestBody @Valid ValidateResetKeyRequest request) {
        log.debug("Called /hotel/account/validate-reset-key.");

        return hotelAuthServiceCdh.validateResetKey(request);
    }

}
