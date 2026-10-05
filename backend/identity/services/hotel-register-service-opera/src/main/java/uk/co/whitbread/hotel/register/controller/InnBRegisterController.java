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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.common.validators.password.PasswordByConfigValidator;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoResponse;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;
import uk.co.whitbread.hotel.register.utils.register.PasswordPolicyUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/hotel-register")
@Tag(name = "InnBusiness Register operations")
public class InnBRegisterController {

  private final HotelRegisterService hotelRegisterService;
  private final PasswordByConfigValidator passwordValidator;

  @Operation(method = "registerInnBStepOne", description = "InnBusiness Register Step One",
      summary = "This endpoint provides InnBusiness register first step functionality")
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
  @PostMapping(path = "/innbusiness/registration/step-one",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<InnBRegistrationStepOneResponse> registerInnBStepOne(
      @Parameter(required = true, name = "payload", description = "The Customer JSON payload") @Valid @RequestBody InnBRegistrationStepOneRequest request) {

    log.debug(
        "Called /v1/hotel-register/innbusiness/registration/step-one (POST -  registerInnBStepOne)");

    InnBRegistrationStepOneResponse customerResponse = hotelRegisterService.registerInnBStepOne(
        request);

    return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
  }

  @Operation(method = "registerInnBStepTwo", description = "InnBusiness Register Step Two",
      summary = "This endpoint provides InnBusiness register second step functionality")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All Good!",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CustomerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(path = "/innbusiness/registration/step-two",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<InnBRegistrationStepTwoResponse> registerInnBStepTwo(
      @Parameter(required = true, name = "payload", description = "The Customer JSON payload") @Valid @RequestBody InnBRegistrationStepTwoRequest request) {

    log.debug(
        "Called /v1/hotel-register/innbusiness/registration/step-two (POST -  createInnBusinessCustomer)");

    PasswordPolicyUtil.enforcePasswordPolicy(passwordValidator, request.getPassword(), false);
    var response = hotelRegisterService.registerInnBStepTwo(request);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
