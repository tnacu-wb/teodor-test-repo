package uk.co.whitbread.ohip.infrastructure.rest.controller.packages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.DonationPackagesRequestDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.DonationPackagesResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.PackageGroupRequestDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.PackageGroupResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.PackagesRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.mapper.PackagesResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.DonationPackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.PackageGroupsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in.PackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.DonationPackagesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackagesGroupResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out.PackagesResponseDto;

@RestController
@RequiredArgsConstructor
public class PackagesController {

  private final PackagesInPort packagesInPort;
  private final PackagesRequestMapper packagesRequestMapper;
  private final PackagesResponseDtoMapper packagesResponseDtoMapper;
  private final DonationPackagesRequestDtoMapper donationPackagesRequestDtoMapper;
  private final DonationPackagesResponseDtoMapper donationPackagesResponseDtoMapper;
  private final PackageGroupResponseDtoMapper packageGroupResponseDtoMapper;
  private final PackageGroupRequestDtoMapper packageGroupsRequestDtoMapper;

  @SuppressWarnings("squid:S6856")
  @Operation(summary = "Retrieves Packages")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/hotels/{hotelId}/packages", produces = MediaType.APPLICATION_JSON_VALUE)
  public PackagesResponseDto getHotelPackages(
      @Valid @ParameterObject PackagesRequestDto packagesRequestDto) {

    final var domainPackageRequest = packagesRequestMapper.toModel(packagesRequestDto);
    final var packagesResponse = packagesInPort.getPackages(domainPackageRequest);

    return packagesResponseDtoMapper.toDto(packagesResponse);
  }

  @SuppressWarnings("squid:S6856")
  @Operation(summary = "Retrieves donations package details")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = DonationPackagesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/hotels/{hotelId}/packages/donations", produces = MediaType.APPLICATION_JSON_VALUE)
  public DonationPackagesResponseDto getHotelDonationsPackagesDetails(
      @Valid @ParameterObject DonationPackagesRequestDto donationPackagesRequestDto) {

    if (CollectionUtils.isEmpty(donationPackagesRequestDto.getPackageCodes())) {
      return DonationPackagesResponseDto.builder().donationPackages(new ArrayList<>()).build();
    }
    final var donationPackagesRequest =
        donationPackagesRequestDtoMapper.toModel(donationPackagesRequestDto);
    final var donationPackagesResponse =
        packagesInPort.getDonationPackagesDetails(donationPackagesRequest);
    return donationPackagesResponseDtoMapper.toDto(donationPackagesResponse);
  }

  @SuppressWarnings("squid:S6856")
  @Operation(summary = "Retrieves Package Groups")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesGroupResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(
      value = "/hotels/packages/groups", produces = MediaType.APPLICATION_JSON_VALUE)
  public PackagesGroupResponseDto getHotelPackageGroups(
      @Valid @RequestBody PackageGroupsRequestDto packageGroupsRequestDto) {
    final var packageGroupRequest = packageGroupsRequestDtoMapper.toModel(packageGroupsRequestDto);
    final var packagesGroupResponse = packagesInPort.getPackagesGroups(packageGroupRequest);
    return packageGroupResponseDtoMapper.toDto(packagesGroupResponse);
  }
}
