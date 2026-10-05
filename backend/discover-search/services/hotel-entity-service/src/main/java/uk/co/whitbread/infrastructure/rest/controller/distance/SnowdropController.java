package uk.co.whitbread.infrastructure.rest.controller.distance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.domain.ports.primary.DistanceFromSearchInPort;
import uk.co.whitbread.infrastructure.rest.client.distance.exception.HotelDistanceException;
import uk.co.whitbread.infrastructure.rest.client.hotelsearch.exception.HotelSearchLocationException;
import uk.co.whitbread.infrastructure.rest.controller.distance.mapper.DistanceFromSearchRequestMapper;
import uk.co.whitbread.infrastructure.rest.controller.distance.mapper.DistanceFromSearchResponseMapper;
import uk.co.whitbread.infrastructure.rest.controller.distance.mapper.HotelLocationSearchResponseMapper;
import uk.co.whitbread.infrastructure.rest.controller.distance.model.in.DistanceFromSearchRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.distance.model.out.DistanceFromSearchResponseDto;
import uk.co.whitbread.infrastructure.rest.controller.distance.model.out.HotelLocationSearchResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SnowdropController {

  private final DistanceFromSearchRequestMapper distanceFromSearchRequestMapper;
  private final DistanceFromSearchResponseMapper distanceFromSearchResponseMapper;
  private final DistanceFromSearchInPort distanceFromSearchInPort;
  private final HotelLocationSearchResponseMapper hotelLocationSearchResponseMapper;

  @Operation(summary = "Retrieves Hotels Distance from search Result")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = DistanceFromSearchResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelDistanceException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelDistanceException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelDistanceException.class))})
  @GetMapping(
      value = "/hotels/{hotelId}/distance", produces = MediaType.APPLICATION_JSON_VALUE)
  public DistanceFromSearchResponseDto getHotelAvailabilities(
      @Valid @PathVariable(value = "hotelId") String hotelId,
      @Valid @ParameterObject DistanceFromSearchRequestDto distanceFromSearchRequestDto) {

    var distanceFromSearchRequest = distanceFromSearchRequestMapper.toModel(hotelId,
        distanceFromSearchRequestDto);

    var distanceFromSearchResponse = distanceFromSearchInPort.getDistanceFromSearch(
        distanceFromSearchRequest);

    return distanceFromSearchResponseMapper.toDto(distanceFromSearchResponse);
  }

  @Operation(summary = "Retrieves Hotels from search Result")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelLocationSearchResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelSearchLocationException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelSearchLocationException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelSearchLocationException.class))})
  @GetMapping(
      value = "/hotels/locations", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<HotelLocationSearchResponseDto> getHotelLocations(
      @Valid @ParameterObject DistanceFromSearchRequestDto distanceFromSearchRequestDto) {

    var distanceFromSearchRequest = distanceFromSearchRequestMapper.toModel(null,
        distanceFromSearchRequestDto);

    var hotelDistancesResponse = distanceFromSearchInPort.getHotelDistancesFromSearch(
        distanceFromSearchRequest);

    return hotelLocationSearchResponseMapper.toDtos(hotelDistancesResponse);
  }
}
