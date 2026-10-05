package uk.co.whitbread.marketing.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequestV2;
import uk.co.whitbread.marketing.service.PermissionManagementService;
import uk.co.whitbread.marketing.utils.RequestUtils;

@Slf4j
@RequestMapping("/internal/marketing/newsletter")
@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "Permission Management API Controller")
public class InternalMarketingPermissionManagementController {

  private final PermissionManagementService permissionManagementService;
  private final RequestUtils requestUtils;

  @Operation(summary = "Newsletter Preferences Create/Update Service", description =
      "Newsletter Subscription - updates newsletter preferences or creates new preferences for a customer.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Success. No content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> updatePermissions(
      @RequestBody @Valid UpdatePreferencesRequestV2 request) {
    requestUtils.validateUpdatePreferences(request, request.getContactValue(),
        ContactType.email);

    log.info("Called PUT /internal/marketing/newsletter for contact type {}.", ContactType.email);

    permissionManagementService.updatePreferences(request, request.getContactValue(),
        ContactType.email);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
