package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp;

import static uk.co.whitbread.digitalkey.domain.utils.SanitizingUtils.sanitize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.in.GoogleWalletProvisioningRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpProvisionRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.RegisterMobileDeviceRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.out.GoogleWalletProvisioningResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpProvisionResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.RegisterMobileDeviceResponse;
import uk.co.whitbread.digitalkey.domain.ports.primary.DigitalKeyInPort;


@RestController
@RequestMapping("/v1/digital-key")
@AllArgsConstructor
@Slf4j
public class DigitalKeyController {

  private final DigitalKeyInPort digitalKeyInPort;


  @Operation(summary = "Generate OTP")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PostMapping("/generate-otp")
  public ResponseEntity<OtpResponse> generateOtp(@RequestBody @Valid OtpRequest request) {
    OtpResponse otpResponse = digitalKeyInPort.generateOtp(request);
    return ResponseEntity.status(HttpStatus.OK).body(otpResponse);
  }

  @Operation(summary = "Digital Key Pass Provision with OTP")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PostMapping("/provision")
  public ResponseEntity<OtpProvisionResponse> passProvisioningWithOtp(@RequestBody @Valid
                                                                      OtpProvisionRequest otpProvisionRequest) {
    OtpProvisionResponse otpProvisionResponse =
        digitalKeyInPort.passProvisioningWithOtp(otpProvisionRequest);
    return ResponseEntity.status(HttpStatus.OK).body(otpProvisionResponse);
  }

  @Operation(summary = "Register Mobile Device For Android Digital Key Provisioning.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterMobileDeviceResponse.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping("/register-mobile-device")
  public ResponseEntity<RegisterMobileDeviceResponse> registerMobileDevice(
          @RequestBody @Valid RegisterMobileDeviceRequest request) {
    log.info("log_register_mobile_device : Registering mobile device for bookingRef={}, deviceId={}",
            sanitize(request.getBookingReference()), sanitize(request.getDeviceId()));
    RegisterMobileDeviceResponse response = digitalKeyInPort.registerMobileDevice(request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Google Wallet Provisioning with OTP.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = GoogleWalletProvisioningResponse.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping("/google-wallet-provisioning-with-otp")
  public ResponseEntity<GoogleWalletProvisioningResponse> googleWalletProvisioningWithOtp(
          @RequestBody @Valid GoogleWalletProvisioningRequest request) {
    log.info("log_google_wallet_provisioning : bookingRef={}, hotelId={}",
            sanitize(request.getBookingReference()), sanitize(request.getShortPropertyCode()));
    GoogleWalletProvisioningResponse response = digitalKeyInPort.googleWalletProvisioningWithOtp(request);
    return ResponseEntity.ok(response);
  }
}
