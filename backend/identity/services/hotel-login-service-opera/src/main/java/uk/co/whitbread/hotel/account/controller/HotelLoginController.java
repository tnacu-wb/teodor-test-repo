package uk.co.whitbread.hotel.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.account.model.LoginRequest;
import uk.co.whitbread.hotel.account.model.LoginResponse;
import uk.co.whitbread.hotel.account.model.LogoutRequest;
import uk.co.whitbread.hotel.account.model.LogoutResponse;
import uk.co.whitbread.hotel.account.model.SessionRequest;
import uk.co.whitbread.hotel.account.model.SessionResponse;
import uk.co.whitbread.hotel.account.model.TokenResponse;
import uk.co.whitbread.hotel.account.service.HotelLoginService;
import uk.co.whitbread.hotel.account.utils.token.SecureTokenGenerator;

import jakarta.validation.Valid;

@Slf4j
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Hotel Authentication operations")
public class HotelLoginController {

    private final HotelLoginService hotelLoginService;
    private final SecureTokenGenerator secureTokenGenerator;

    @Operation(
            method = "initiateSession",
            description = "Customer session creation by encrypted guest history number",
            summary = "Initiates a session for a customer using its guest history number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Good!"),
            @ApiResponse(responseCode = "401", description = "Invalid Credentials: Wrong guest history number",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/session",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SessionResponse initiateSession(@Parameter(required = true) @Valid @RequestBody SessionRequest request,
                                           @RequestParam(required = false) boolean business) {
        log.info("Called POST /auth/session");
        return hotelLoginService.initiateSession(request, business);

    }

    /**
     * @deprecated The preferred way of signing in/out is externally through Auth0
     */
    @Deprecated
    @Operation(method = "login", description = "Customer Login", summary = "Sign in to an existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Good!"),
            @ApiResponse(responseCode = "401", description = "Invalid Credentials: Wrong username or password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/hotels/login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse login(@Parameter(required = true) @Valid @RequestBody LoginRequest request,
                               @Parameter @RequestParam(required = false) boolean business) {
        log.info("Called /auth/hotels/login (b:{})", business);
        return hotelLoginService.login(request, business);
    }

    /**
     * @deprecated The preferred way of signing in/out is externally through Auth0
     */
    @Deprecated
    @Operation(summary = "Logout", description = "Sign out from an open session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/hotels/logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public LogoutResponse logout(
            @Valid @RequestBody LogoutRequest request) {
        log.info("Called /auth/hotels/logout");
        return hotelLoginService.logout(request);
    }

    @Operation(summary = "SecureTokenGenerator", description = "Returns a new random token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/hotels/token/{length}",
            method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenResponse getToken(
            @Parameter(required = true) @PathVariable("length") Integer length) {
        log.info("Called /auth/hotels/token/" + length);

        return new TokenResponse(secureTokenGenerator.generateToken(length));
    }
}