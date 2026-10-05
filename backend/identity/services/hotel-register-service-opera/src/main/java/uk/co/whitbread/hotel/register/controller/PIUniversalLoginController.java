package uk.co.whitbread.hotel.register.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.register.model.RegisterAccountRequest;
import uk.co.whitbread.hotel.register.model.RegisterAccountResponse;
import uk.co.whitbread.hotel.register.security.ApiKeyProtected;
import uk.co.whitbread.hotel.register.service.PIUniversalLoginService;

import static uk.co.whitbread.hotel.register.utils.register.Utils.sanitizeInputString;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/hotel-register/universal-login/leisure")
@Tag(name = "Universal Login operations")
@Validated
public class PIUniversalLoginController {

  private final PIUniversalLoginService universalLoginService;

  @Operation(method = "registerAccount", description = "Register Account",
      summary = "This endpoint registers a new account in CDH")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "All Good!",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = RegisterAccountResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Invalid input",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Server Error: Something went wrong",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(path = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiKeyProtected
  public ResponseEntity<RegisterAccountResponse> registerAccount(
          @Valid @RequestBody RegisterAccountRequest payload
  ) {
    log.info("Received email: {}", sanitizeInputString(payload.getEmail()));
    var registerAccountResponse = universalLoginService.registerAccount(payload);
    log.info("Returning new customer response: {}", registerAccountResponse);
    return new ResponseEntity<>(registerAccountResponse, HttpStatus.CREATED);
  }
}
