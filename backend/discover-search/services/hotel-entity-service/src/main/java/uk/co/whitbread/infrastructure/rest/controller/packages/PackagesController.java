package uk.co.whitbread.infrastructure.rest.controller.packages;

import static uk.co.whitbread.infrastructure.rest.client.utils.SanitizingUtils.sanitizeForLog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.infrastructure.rest.client.packages.exceptions.PackagesException;
import uk.co.whitbread.infrastructure.rest.controller.packages.mapper.DonationPackagesRequestDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.packages.mapper.DonationPackagesResponseDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.packages.mapper.PackagesRequestDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.packages.mapper.PackagesResponseDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.in.DonationPackagesRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.in.PackagesRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.DonationPackagesResponseDto;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.PackagesResponseDto;

/**
 * Hotel Availability Entity REST Controller. Used to forward Experience requests for availabilities
 * of hotel rooms to OHIP Adapter.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PackagesController {

  private final PackagesInPort packagesInPort;
  private final PackagesResponseDtoMapper packagesResponseDtoMapper;
  private final PackagesRequestDtoMapper packagesRequestDtoMapper;
  private final DonationPackagesRequestDtoMapper donationPackagesRequestDtoMapper;
  private final DonationPackagesResponseDtoMapper donationPackagesResponseDtoMapper;

  @Operation(summary = "Retrieves Packages")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesException.class))})
  @GetMapping(value = "/hotels/{hotelId}/packages", produces = MediaType.APPLICATION_JSON_VALUE)
  @SuppressWarnings("squid:S6856")
  public PackagesResponseDto getHotelPackages(
      @Valid @ParameterObject PackagesRequestDto packagesRequestDto) {

    log.debug("Request to get packages for: {}", sanitizeForLog(packagesRequestDto));
    final var domainPackagesRequest = packagesRequestDtoMapper.toModel(packagesRequestDto);
    final var domainPackagesResponse = packagesInPort.getPackages(domainPackagesRequest);

    return packagesResponseDtoMapper.toDto(domainPackagesResponse);

  }

  @Operation(summary = "Retrieves Donations Packages Details")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PackagesException.class))})
  @GetMapping(value = "/hotels/{hotelId}/packages/donations", produces = MediaType.APPLICATION_JSON_VALUE)
  @SuppressWarnings("squid:S6856")
  public DonationPackagesResponseDto getHotelCharityPackagesDetails(
      @Valid @ParameterObject final DonationPackagesRequestDto donationPackagesRequestDto) {

    log.debug("Request to get donationPackages for: {}", sanitizeForLog(donationPackagesRequestDto));
    final var donationPackagesRequest = donationPackagesRequestDtoMapper.toModel(
        donationPackagesRequestDto);
    final var donationPackagesResponse = packagesInPort.getDonationPackageDetails(
        donationPackagesRequest);

    return donationPackagesResponseDtoMapper.toDto(donationPackagesResponse);

  }
}
