package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AddApplicationCardRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AppCompanyDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ApplicationDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DeleteCardRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DeletePayApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DirectDebitRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetAppCardsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetAppLookupRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetDdSepaFormStatusRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetUserPreferencesRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.InitializeApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.RemoveParticipantRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ShareAppRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.SubmitApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateAppCompanyDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateAppContactDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateResumeUrlRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AddApplicationCardResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AppCompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AppPreCheckResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ApplicationDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.DeletePayApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.DirectDebitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.FetchApplicationsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetAppCardsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetDdSepaFormStatusResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetUserPreferencesResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.InitializeApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ShareAppResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.SubmitApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateAppCompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateAppContactDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateResumeUrlResponseDto;

interface PayAppApi {

  @Operation(summary = "Initializes an InnBusiness Pay application")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = InitializeApplicationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<InitializeApplicationResponseDto> initializeApplication(
      InitializeApplicationRequestDto initializeApplicationRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Retrieves information about InnBusiness applications associated with the logged-in user")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = FetchApplicationsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<FetchApplicationsResponseDto> fetchPayApplications(
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Update contact details of an InnBusiness application associated with the logged-in user")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = UpdateAppContactDetailsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<UpdateAppContactDetailsResponseDto> updateAppContactDetails(
      UpdateAppContactDetailsRequestDto updateAppContactDetailsRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Update company details of an InnBusiness application associated with the logged-in user")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = UpdateAppCompanyDetailsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<UpdateAppCompanyDetailsResponseDto> updateAppCompanyDetails(
      UpdateAppCompanyDetailsRequestDto updateAppCompanyDetailsRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Triggers the deletion of an InnBusiness Pay application")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = DeletePayApplicationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<DeletePayApplicationResponseDto> deleteApplication(
      @Valid @RequestBody DeletePayApplicationRequestDto requestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Retrieves static application related lookup data")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = Map.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  Map<String, List<String>> getAppLookup(
      GetAppLookupRequestDto getAppLookupRequestDto, HttpServletRequest httpServletRequest);

  @Operation(summary = "Lookup company details")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AppCompanyDetailsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<AppCompanyDetailsResponseDto> lookupCompanyDetails(
      AppCompanyDetailsRequestDto appCompanyDetailsRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Fetches InnBusiness application user preferences for the logged-in user,"
      + " based on their tetheredUserGuid")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = GetUserPreferencesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<List<GetUserPreferencesResponseDto>> getUserPreferences(
      @Valid @ParameterObject GetUserPreferencesRequestDto userPreferencesRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Fetch details about InnBusiness application for the logged-in user")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApplicationDetailsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<ApplicationDetailsResponseDto> getApplicationDetails(
      @Valid @ParameterObject ApplicationDetailsRequestDto applicationDetailsRequestDto,
      HttpServletRequest httpServletRequest);


  @Operation(summary = "Add a card to an InnBusiness application for the logged-in user")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AddApplicationCardResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<AddApplicationCardResponseDto> addApplicationCard(
      @Valid @RequestBody AddApplicationCardRequestDto addApplicationCardRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Share an InnBusiness application with the specified user")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ShareAppResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<ShareAppResponseDto> shareApp(ShareAppRequestDto shareAppRequestDto);

  @Operation(summary = "Removes a specified participant from an application")
  @ApiResponse(responseCode = "204", description = "No Content")
  @ApiResponse(responseCode = "400", description = "Error Occurred", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<Void> removeParticipant(
      @Valid @RequestBody RemoveParticipantRequestDto removeParticipantRequestDto);

  @Operation(summary = "Deletes a specified card for an application")
  @ApiResponse(responseCode = "204", description = "No Content")
  @ApiResponse(responseCode = "400", description = "Error Occurred", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<Void> deleteApplicationCard(@Valid @RequestBody DeleteCardRequestDto deleteCardRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Fetch details about the cards associated with an InnBusiness application")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = GetAppCardsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<GetAppCardsResponseDto> getApplicationCards(
      GetAppCardsRequestDto getAppCardsRequestDto, HttpServletRequest httpServletRequest);


  @Operation(summary = "Submits an InnBusiness application for the logged-in user")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = SubmitApplicationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<SubmitApplicationResponseDto> submitApplication(
      SubmitApplicationRequestDto submitApplicationRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Checks if the user's e-mail address is associated with an existing Account Holder User")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AppPreCheckResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<AppPreCheckResponseDto> appPreCheck(
      Scheme scheme,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Direct debit option for an InnBusiness application")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = DirectDebitResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<DirectDebitResponseDto> directDebit(DirectDebitRequestDto directDebitRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Updates the resume URL for an InnBusiness application")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = UpdateResumeUrlResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<UpdateResumeUrlResponseDto> updateResumeUrl(UpdateResumeUrlRequestDto updateResumeUrlRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Get the bank details status associated with an InnBusiness application")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = GetDdSepaFormStatusResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<GetDdSepaFormStatusResponseDto> getDdSepaFormStatus(
      GetDdSepaFormStatusRequestDto getDdSepaFormStatusRequestDto, HttpServletRequest httpServletRequest);
}
