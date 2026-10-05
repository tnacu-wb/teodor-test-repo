package uk.co.whitbread.hotel.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.account.model.CustomerResponse;
import uk.co.whitbread.hotel.account.model.PasswordResetResponse;
import uk.co.whitbread.hotel.account.model.SendEmailRequest;
import uk.co.whitbread.hotel.account.security.ApiKeyProtected;
import uk.co.whitbread.hotel.account.service.UniversalLoginService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/hotel-account/universal-login")
@Tag(name = "Universal Login operations", description = "Endpoints for universal login functionality")
@Validated
public class UniversalLoginController {

  private final UniversalLoginService universalLoginService;

  @Operation(method = "sendAccountEmail", description = "Send email to leisure account",
      summary = "This endpoint sends an email to the end-user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "All Good!",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CustomerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Invalid input",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Server Error: Something went wrong",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(path = "/leisure/accounts/{email}/send-email",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiKeyProtected
  public ResponseEntity<Void> sendEmailToLeisureAccount(@Parameter(required = true, example = "user@example.com")
      @PathVariable @Email(message = "Invalid email format") String email,
      @Parameter(required = true) @RequestBody @Valid SendEmailRequest payload) {

    universalLoginService.sendEmailToLeisureAccount(email, payload);

    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @Operation(method = "getPasswordResetUrl", description = "Get the password reset url for leisure account",
      summary = "This endpoint will fetch the password reset url from auth0")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "All Good!",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CustomerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Invalid input",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Server Error: Something went wrong",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(path = "/leisure/accounts/{email}/password-reset", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<PasswordResetResponse> getPasswordResetUrl(
      @Parameter(required = true, example = "user@example.com")
      @PathVariable @Email(message = "Invalid email format") String email) {
    var response = universalLoginService.getPasswordResetUrl(email);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}

