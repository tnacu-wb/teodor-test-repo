package uk.co.whitbread.infrastructure.rest.controller.srp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.domain.ports.primary.HotelAvailabilitiesInPort;
import uk.co.whitbread.infrastructure.rest.controller.srp.mapper.HotelAvailabilitiesRequestDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.srp.mapper.HotelAvailabilitiesResponseDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.HotelAvailabilitiesRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.out.HotelAvailabilitiesResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
//@RequestMapping("/v1")
public class SearchResultsPageController {

  private final HotelAvailabilitiesInPort hotelSearchInPort;
  private final HotelAvailabilitiesInPort hotelSearchInPortV2;
  private final HotelAvailabilitiesRequestDtoMapper hotelAvailabilitiesRequestDtoMapper;
  private final HotelAvailabilitiesResponseDtoMapper hotelAvailabilitiesResponseDtoMapper;

  @GetMapping(
      value = "/v1/hotels/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HotelAvailabilitiesResponseDto> getSrpHotelAvailabilities(
      @Valid @ParameterObject HotelAvailabilitiesRequestDto hotelAvailabilitiesRequestDto) {

    var hotelAvailabilitiesRequest = hotelAvailabilitiesRequestDtoMapper.toModel(
        hotelAvailabilitiesRequestDto);

    var hotelAvailabilitiesResponse = hotelSearchInPort.getAvailabilities(
        hotelAvailabilitiesRequest);
    hotelAvailabilitiesResponse.setPage(hotelAvailabilitiesRequestDto.getPage());

    return ResponseEntity.ok(hotelAvailabilitiesResponseDtoMapper.toDtos(hotelAvailabilitiesResponse));
  }

  @GetMapping(
      value = "/v2/hotels/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelAvailabilitiesResponseDto getSrpHotelAvailabilitiesV2(
      @Valid @ParameterObject HotelAvailabilitiesRequestDto hotelAvailabilitiesRequestDto) {

    var hotelAvailabilitiesRequest = hotelAvailabilitiesRequestDtoMapper.toModel(
        hotelAvailabilitiesRequestDto);

    var hotelAvailabilitiesResponse = hotelSearchInPortV2.getAvailabilities(
        hotelAvailabilitiesRequest);

    return hotelAvailabilitiesResponseDtoMapper.toDtos(hotelAvailabilitiesResponse);
  }
}
