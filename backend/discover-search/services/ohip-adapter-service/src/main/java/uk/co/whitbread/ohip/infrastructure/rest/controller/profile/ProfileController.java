package uk.co.whitbread.ohip.infrastructure.rest.controller.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.ProfileInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.AddProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.CompaniesProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.CompanyProfileResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.CreateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.UpdateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.CompaniesProfileRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.ProfileStayingGuestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.RawRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.UpdateProfileRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out.CompaniesProfileDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out.CompanyProfileDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/profile")
public class ProfileController {

  private final CreateProfileRequestMapper createProfileRequestMapper;
  private final CompaniesProfileRequestMapper companiesProfileRequestMapper;
  private final CompanyProfileResponseMapper companyProfileResponseMapper;
  private final AddProfileRequestMapper addProfileRequestMapper;
  private final UpdateProfileRequestMapper updateProfileRequestMapper;
  private final ProfileInPort profileInPort;

  @Operation(summary = "Creating and Adding a Profile to the Reservation")
  @ApiResponse(responseCode = "204", description = "No Content")
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/createProfile", produces = MediaType.APPLICATION_JSON_VALUE)
  public void createProfileKiosk(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId,
      @RequestBody ProfileStayingGuestDetailsDto stayingGuestDetailsDto) {
    final var stayingGuestDetails = createProfileRequestMapper.toProfileRequestModel(
        stayingGuestDetailsDto);
    final var profileIds = profileInPort.createProfile(stayingGuestDetails, hotelId,
        reservationId);
    final var addProfileRequest = addProfileRequestMapper.toAddProfileRequestModel(
        RawRequestDto.builder().profileId(profileIds).reservationId(reservationId).build());
    profileInPort.addProfile(addProfileRequest, hotelId);
  }

  @Operation(summary = "Getting companies based on search criteria")
  @ApiResponse(responseCode = "200", description = "Success",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = CompaniesProfileDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/companies", produces = MediaType.APPLICATION_JSON_VALUE)
  public CompaniesProfileDto getCompaniesProfile(
      @Validated @ParameterObject CompaniesProfileRequestDto companiesProfileRequestDto) {

    final var companiesProfileRequest =
        companiesProfileRequestMapper.toCompaniesProfileDomainModel(companiesProfileRequestDto);
    var companiesProfile = profileInPort.getCompaniesProfile(companiesProfileRequest);
    return companiesProfileRequestMapper.toCompaniesProfileDto(companiesProfile);
  }

  @Operation(summary = "Get company profile by corporate id")
  @ApiResponse(responseCode = "200", description = "Success",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = CompaniesProfileDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/company/{corporateId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public CompanyProfileDto getCompanyProfileByCorporateId(@PathVariable final String corporateId) {
    var companyProfile = profileInPort.getCompanyProfileByCorporateId(corporateId);
    return companyProfileResponseMapper.toCompanyProfileDto(companyProfile);
  }

  @Operation(summary = "Get company profile by company id")
  @ApiResponse(responseCode = "200", description = "Success",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = CompaniesProfileDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/company/id/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public CompanyProfileDto getCompanyProfileByCompanyId(@PathVariable final String companyId) {
    var companyProfile = profileInPort.getCompanyProfileByCompanyId(companyId);
    return companyProfileResponseMapper.toCompanyProfileDto(companyProfile);
  }

  @Operation(summary = "Updating a Profile in the Reservation")
  @ApiResponse(responseCode = "204", description = "No Content")
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/updateProfile", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateProfileKiosk(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId,
      @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
    profileInPort.updateProfile(hotelId, reservationId,
        updateProfileRequestMapper.toUpdateProfileRequestModel(updateProfileRequestDto));
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

}
