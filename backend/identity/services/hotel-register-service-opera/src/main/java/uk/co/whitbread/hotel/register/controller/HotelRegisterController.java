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
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.common.validators.password.PasswordByConfigValidator;
import uk.co.whitbread.hotel.captcha.exception.CaptchaVerificationException;
import uk.co.whitbread.hotel.captcha.service.CaptchaService;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;
import uk.co.whitbread.hotel.register.utils.register.PasswordPolicyUtil;
import uk.co.whitbread.hotel.register.validation.BusinessCustomerValidator;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/customers/hotels")
@Tag(name = "Hotel Register operations")
public class HotelRegisterController {

  private final HotelRegisterService hotelRegisterService;
  private final CaptchaService captchaService;
  private final BusinessCustomerValidator businessCustomerValidator;
  private final PasswordByConfigValidator passwordValidator;

  @Operation(method = "createCustomer", description = "Create Customer",
      summary = "This endpoint provides the functionality to create a new customer resource")
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
  @RequestMapping(method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<CustomerResponse> createCustomer(
      @Parameter(required = true, name = "payload", description = "The Customer JSON payload") @Valid @RequestBody Customer payload,
      @Parameter @RequestHeader(name = "language", required = false) String languageCode,
      @Parameter @RequestParam(required = false, defaultValue = "false") boolean business) {

    log.debug("Called /customers/hotels (POST -  createCustomer) (b:{})", business);

    if (payload.getCaptcha() != null && !captchaService.isValid(payload.getCaptcha())) {
      throw new CaptchaVerificationException("Captcha verification failed");
    }

    if (business) {
      businessCustomerValidator.validate(payload);
    }

    PasswordPolicyUtil.enforcePasswordPolicy(passwordValidator, payload.getPassword(), false);

    log.info("Creating customer");
    CustomerResponse customerResponse = hotelRegisterService.createCustomer(payload, languageCode,
        business);

    return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
  }

}
