package uk.co.whitbread.marketing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.bart.enums.BartBookingChannelCode;
import uk.co.whitbread.bart.enums.BartHotelBrandCode;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetResponse;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetResponse;
import uk.co.whitbread.marketing.model.permissionmanagement.ConfirmDoubleOptInRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UnsubscribeRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;
import uk.co.whitbread.marketing.service.AuthenticationService;
import uk.co.whitbread.marketing.service.CustomerHubService;
import uk.co.whitbread.marketing.service.PermissionManagementService;
import uk.co.whitbread.marketing.utils.RequestUtils;

import static uk.co.whitbread.marketing.client.hotelaccount.HotelAccountClient.DEFAULT_BRAND;
import static uk.co.whitbread.marketing.client.hotelaccount.HotelAccountClient.DEFAULT_PI_BOOKING_CHANNEL;

@Slf4j
@RequestMapping("/marketing/newsletter")
@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Permission Management API Controller")
public class MarketingPermissionManagementController {

  private final PermissionManagementService permissionManagementService;
  private final CustomerHubService customerHubService;
  private final RequestUtils requestUtils;
  private final AuthenticationService authenticationService;

  @Operation(description = "Get Newsletter Preferences Authenticated", summary =
      "Newsletter Subscription - Used to retrieve newsletter preferences using the contact channel value."
          +
          "This endpoint is authenticated to prevent access of another customers newsletter persmissions using their email or phone.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PreferencesGetResponse.class))),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameters({
      @Parameter(in = ParameterIn.PATH, name = "contactType", required = true,
          content = @Content(schema = @Schema(type = "string", allowableValues = {"email",
              "phone"}, defaultValue = "email"))),
      @Parameter(in = ParameterIn.PATH, name = "contactValue", required = true,
          content = @Content(schema = @Schema(type = "string", defaultValue = "test@validemail.com"))),
      @Parameter(in = ParameterIn.HEADER, name = "Authentication",
          content = @Content(schema = @Schema(type = "string", defaultValue = "Bearer sdfghjdfgh"))),
      @Parameter(in = ParameterIn.QUERY, name = "brandCodes", description = "Array of brand codes.",
          content = @Content(schema = @Schema(type = "string", allowableValues = {"BARB", "BEEF",
              "BREW", "COOK", "PGER", "PHUB", "PINN", "PZIP", "TABL", "TAYB", "WINN"}))),
      @Parameter(in = ParameterIn.QUERY, name = "countryOfResidence", description = "The country of residence of the guest",
          content = @Content(schema = @Schema(type = "string", defaultValue = "gb"))),
      @Parameter(in = ParameterIn.QUERY, name = "language", description = "Language",
          content = @Content(schema = @Schema(type = "string", defaultValue = "en")))
  })
  @GetMapping(value = "{contactType}/{contactValue}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PreferencesGetResponse> getPermissions(
      @RequestHeader(name = "hotel-brand", required = false, defaultValue = DEFAULT_BRAND) BartHotelBrandCode hotelBrand,
      @RequestHeader(required = false, defaultValue = DEFAULT_PI_BOOKING_CHANNEL) BartBookingChannelCode bookingChannel,
      @RequestHeader(name = "Authentication") String authentication,
      @RequestHeader(name = "Origin", required = false) String origin,
      @Valid PreferencesGetRequest request) {

    requestUtils.validateGetPreferencesRequest(request);
    authenticationService.validate(authentication, request.getContactType(),
        request.getContactValue(), hotelBrand, bookingChannel, request.isBusiness(), origin);
    log.info("Called GET /marketing/newsletter/{}/*****, brandCodes: {}",
        request.getContactType(), request.getBrandCodes());
    PreferencesGetResponse response = permissionManagementService.getPreferences(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(description = "Get Newsletter Preferences Non-authenticated", summary =
      "Newsletter Subscription - Used to retrieve newsletter preferences using the email address value.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PreferencesGetResponse.class))),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameters({
      @Parameter(in = ParameterIn.PATH, name = "emailAddress", required = true,
          content = @Content(schema = @Schema(type = "string", defaultValue = "test@validemail.com"))),
      @Parameter(in = ParameterIn.QUERY, name = "brandCode", description = "Brand code value",
          content = @Content(schema = @Schema(type = "string", allowableValues = {"BARB", "BEEF",
              "BREW", "COOK", "PGER", "PHUB", "PINN", "PZIP", "TABL", "TAYB", "WINN"}))),
      @Parameter(in = ParameterIn.QUERY, name = "countryOfResidence", description = "The country of residence of the guest",
          content = @Content(schema = @Schema(type = "string", defaultValue = "gb"))),
      @Parameter(in = ParameterIn.QUERY, name = "language", description = "Language",
          content = @Content(schema = @Schema(type = "string", defaultValue = "en")))
  })
  @GetMapping(value = "{emailAddress}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PreferencesAnonymousGetResponse> getPermissionsAnonymous(
      @Valid PreferencesAnonymousGetRequest request) {
    requestUtils.validateBrandCodes(request.getBrandCode());
    log.info("Called GET /marketing/newsletter/*****, brandCode: {}", request.getBrandCode());
    var response = permissionManagementService.getPreferencesAnonymous(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(summary = "Get Newsletter Preferences using channel id", description = "Newsletter Subscription - Used to retrieve newsletter preferences using the contact channel id.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PreferencesGetResponse.class))),// response = PreferencesGetResponse.class),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})//response = ErrorResponse.class)})
  @Parameters({
      @Parameter(in = ParameterIn.PATH, name = "contactType",
          content = @Content(schema = @Schema(type = "string", allowableValues = {"email", "phone"}, defaultValue = "email"))
      ),
      @Parameter(in = ParameterIn.PATH, name = "contactChannelId", description = "CHNL1880_ed84b98a-59c5-40e0-be5a-10544c289ea5",
          required = true, content = @Content(schema = @Schema(type = "string", defaultValue = "CHNL1880_ed84b98a-59c5-40e0-be5a-10544c289ea5"))),
      @Parameter(in = ParameterIn.PATH, name = "brandCodes", description = "Array of brand codes.",
          content = @Content(schema = @Schema(type = "string", allowableValues = {"BARB", "BEEF",
              "BREW", "COOK", "PGER", "PHUB", "PINN", "PZIP", "TABL", "TAYB", "WINN"})))
  })
  @GetMapping(value = "{contactType}/channel/{contactChannelId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PreferencesGetResponse> getPermissionsUsingContactChannelId(
      @Valid PreferencesGetRequest request) {
    requestUtils.validateGetPreferencesRequestWithContactChannelId(request);
    log.info("Called GET /marketing/newsletter/******/channel/{}, brandCodes: {}",
        request.getContactChannelId(), request.getBrandCodes());
    PreferencesGetResponse response = permissionManagementService.getPreferencesUsingContactChannelId(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Operation(summary = "Newsletter Preferences Create/Update Service", description =
      "Newsletter Subscription - updates newsletter preferences or creates new preferences for a customer. "
          +
          "If contactType = phone then contactSubType is mandatory.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Success. No content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameters({
      @Parameter(name = "contactType", required = true,
          content = @Content(schema = @Schema(type = "string", allowableValues = {"email",
              "phone"}, defaultValue = "email"))),
      @Parameter(name = "contactValue", description = "Value of contactType", example = "test@validemail.com|07777777777",
          content = @Content(schema = @Schema(type = "string", defaultValue = "test@validemail.com")))
  })
  @PutMapping(value = "{contactType}/{contactValue}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> updatePermissions(
      @RequestBody @Valid UpdatePreferencesRequest request,
      @PathVariable @NotBlank String contactValue,
      @PathVariable @NotNull ContactType contactType) {

    requestUtils.validateUpdatePreferences(request, contactValue, contactType);
    log.info("Called PUT /marketing/newsletter for contact type {}.", contactType);
    if (contactType.equals(ContactType.phone)) {
      log.info("Routing contact type {} to legacy customer Hub.",contactType.getType());
      customerHubService.editNewsletterPreferences(request, contactValue, contactType);
    } else {
      permissionManagementService.updatePreferences(request, contactValue, contactType);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Newsletter Preferences Create/Update Service by contact channel id", description =
      "Newsletter Subscription - updates newsletter preferences or creates new preferences for a customer. ")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Success. No content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameters(
      @Parameter(name = "contactChannelId", description = "Value of contactType", example = "CHNL97722_8415a943-6800-4fbb-808d-0ce878554e64",
          content = @Content(schema = @Schema(type = "string", defaultValue = "CHNL97722_8415a943-6800-4fbb-808d-0ce878554e64"))))
  @PutMapping(value = "/channel/{contactChannelId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> updatePermissionsByContactChannelId(
      @RequestBody @Valid UpdatePreferencesRequest request,
      @PathVariable @NotBlank String contactChannelId) {
    requestUtils.validateUpdatePreferences(request, contactChannelId,
        ContactType.email_contact_channel_id);
    permissionManagementService.updatePreferences(request, contactChannelId,
        ContactType.email_contact_channel_id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Newsletter Preferences Confirm Double Opt In Service", description =
      "Customer will receive an email to confirm they are opting in" +
          "to newsletters. This email will contain a link to the permission centre which will call this endpoint. ")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Success. No content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameters({
      @Parameter(in = ParameterIn.PATH, name = "contactChannelId", description = "Value of contactChannelId", example = "CHNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3",
          required = true, content = @Content(schema = @Schema(type = "string")))
  })
  @PostMapping(value = "/channel/{contactChannelId}/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> confirmDoubleOptIn(
      @RequestBody @Valid ConfirmDoubleOptInRequest request,
      @PathVariable @NotBlank String contactChannelId) {

    requestUtils.validateConfirmDoubleOptIn(request, contactChannelId, request.getContactType());
    log.info("Called POST /marketing/newsletter/channel/{}/confirm.", contactChannelId);
    permissionManagementService.confirmDoubleOptIn(request, contactChannelId,
        request.getContactType());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Newsletter Preferences Unsubscribe Service", description =
      "Customer will receive marketing emails that they have signed up to. " +
          "This email will contain a link to the permission centre for unsubscribing which will call this endpoint. ")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Success. No content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameters({
      @Parameter(in = ParameterIn.PATH, name = "contactChannelId", description = "Value of contactChannelId", example = "CHNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3",
          required = true, content = @Content(schema = @Schema(type = "string")))
  })
  @DeleteMapping(value = "/channel/{contactChannelId}/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> unsubscribe(@RequestBody @Valid UnsubscribeRequest request,
      @PathVariable @NotBlank String contactChannelId) {
    requestUtils.validateUnsubscribeRequest(request, contactChannelId, request.getContactType());
    log.info("Called DELETE /marketing/newsletter/channel/{}/unsubscribe.", contactChannelId);
    permissionManagementService.unsubscribe(request, contactChannelId, request.getContactType());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


  @Operation(summary = "Newsletter Preferences Create/Update Service", description =
      "Newsletter Subscription - updates newsletter preferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Success. No content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PutMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> updateEmailPermissions(
      @RequestHeader(name = "Authorization") String jwt,
      @RequestBody @Valid UpdatePreferencesRequest request) {
    var email = authenticationService.extractAndValidateEmailFromJwt(jwt);
    permissionManagementService.updatePreferences(request, email, ContactType.email);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
