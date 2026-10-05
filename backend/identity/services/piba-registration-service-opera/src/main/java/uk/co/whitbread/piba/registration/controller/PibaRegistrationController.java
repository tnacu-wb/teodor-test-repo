package uk.co.whitbread.piba.registration.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationRequest;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationResponse;
import uk.co.whitbread.piba.registration.model.RegistrationInfoRequest;
import uk.co.whitbread.piba.registration.model.RegistrationInfoResponse;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitRequest;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitResp;
import uk.co.whitbread.piba.registration.service.PibaRegistrationService;
import uk.co.whitbread.piba.registration.util.LogUtils;

@Slf4j
@RequestMapping("/piba/registration")
@RestController
@AllArgsConstructor
@Tag(name = "Piba Registration operations")
public class PibaRegistrationController {

  private final PibaRegistrationService pibaRegistrationService;

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = RegistrationInfoResponse.class))),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @GetMapping(value = "/info/{registration-code}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<RegistrationInfoResponse> registrationGetInfo(
      @RequestHeader(name = "Authorization") String authorization,
      @PathVariable(value = "registration-code") String registrationCode) {

    log.info("Called GET /piba/registration/info with  registrationCode={}",
        registrationCode);
    RegistrationInfoRequest registrationInfoRequest = new RegistrationInfoRequest();
    registrationInfoRequest.setRegistrationCode(registrationCode);
    RegistrationInfoResponse registrationInfoResponse = pibaRegistrationService.getInfo(
        registrationInfoRequest.getRegistrationCode());

    return new ResponseEntity<>(registrationInfoResponse, HttpStatus.OK);

  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = RegistrationAuthenticationResponse.class))),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RegistrationAuthenticationResponse> registrationAuthenticate(
      @RequestHeader(name = "Authorization") String authorization,
      @Parameter(required = true, name = "payload", description = "The registration authenticate JSON payload") @Valid @RequestBody RegistrationAuthenticationRequest registrationAuthenticationRequest) {

    log.info("Called POST /piba/registration/authenticate with registrationCode={}",
        LogUtils.sanitisedStringWithMaxLengthLimit(
            registrationAuthenticationRequest.getRegistrationCode(), 50));

    RegistrationAuthenticationResponse registrationAuthenticationResponse = pibaRegistrationService.authenticate(
        registrationAuthenticationRequest);

    return new ResponseEntity<>(registrationAuthenticationResponse, HttpStatus.CREATED);

  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = RegistrationSubmitResp.class))),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RegistrationSubmitResp> registrationSubmit(
      @RequestHeader(name = "Authorization") String authorization,
      @Parameter(required = true, name = "payload", description = "The registration submit JSON payload") @Valid @RequestBody RegistrationSubmitRequest registrationSubmitRequest) {

    log.info("Called POST /piba/registration/submit with registrationCode={}",
        LogUtils.sanitisedStringWithMaxLengthLimit(registrationSubmitRequest.getRegistrationCode(),
            50));

    RegistrationSubmitResp registrationSubmitResp =
        pibaRegistrationService.submit(registrationSubmitRequest, authorization);
    return new ResponseEntity<>(registrationSubmitResp, HttpStatus.CREATED);

  }
}
