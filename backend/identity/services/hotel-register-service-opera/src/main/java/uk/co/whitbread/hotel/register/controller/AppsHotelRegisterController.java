package uk.co.whitbread.hotel.register.controller;


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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.common.validators.password.PasswordByConfigValidator;
import uk.co.whitbread.hotel.captcha.exception.CaptchaVerificationException;
import uk.co.whitbread.hotel.captcha.service.CaptchaService;
import uk.co.whitbread.hotel.register.model.AppsCustomer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;
import uk.co.whitbread.hotel.register.utils.register.PasswordPolicyUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/hotel-register/accounts/register")
@Tag(name = "Apps Register operations")
public class AppsHotelRegisterController {

  private final HotelRegisterService hotelRegisterService;
  private final CaptchaService captchaService;
  private final PasswordByConfigValidator passwordValidator;

  @Operation(method = "registerAccount", description = "Register Account",
      summary = "This endpoint provides App register functionality")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "All Good!",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CustomerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<CustomerResponse> registerAccount(
      @Parameter(required = true, name = "payload", description = "The Customer JSON payload") @Valid @RequestBody AppsCustomer payload,
      @Parameter @RequestHeader(name = "language", required = false) String languageCode) {

    log.debug("Called /customers/hotels (POST -  registerAccount)");

    if (payload.getCaptcha() != null && !captchaService.isValid(payload.getCaptcha())) {
      throw new CaptchaVerificationException("Captcha verification failed");
    }

    PasswordPolicyUtil.enforcePasswordPolicy(passwordValidator, payload.getPassword(), false);

    log.info("Creating customer");
    CustomerResponse customerResponse = hotelRegisterService.registerAccount(payload, languageCode);

    return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
  }

}