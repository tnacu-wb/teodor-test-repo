package uk.co.whitbread.infrastructure.rest.client.distance;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.model.distance.in.DistanceFromSearchRequest;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.ports.secondary.DistanceFromSearchOutPort;
import uk.co.whitbread.infrastructure.rest.client.SnowDropClient;
import uk.co.whitbread.infrastructure.rest.client.distance.exception.HotelDistanceException;
import uk.co.whitbread.infrastructure.rest.client.distance.exception.SnowDropException;
import uk.co.whitbread.infrastructure.rest.client.distance.mapper.HotelLocationRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.distance.mapper.HotelLocationResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.distance.model.in.HotelLocationRequest;
import uk.co.whitbread.infrastructure.rest.client.distance.model.in.LocationFormat;
import uk.co.whitbread.infrastructure.rest.client.distance.model.out.HotelLocationResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistanceFromSearchOutPortImpl implements DistanceFromSearchOutPort {

  private final SnowDropClient snowDropClient;
  private final HotelLocationRequestMapper hotelLocationRequestMapper;
  private final HotelLocationResponseMapper hotelLocationResponseMapper;

  @Override
  public DistanceFromSearchResponse getDistanceFromSearch(
      DistanceFromSearchRequest distanceFromSearchRequest) {
    log.debug(
        "Entered getDistanceFromSearch with hotelId={}, location={}, locationFormat={},"
            + " radius={}, radiusUnit={}",
        distanceFromSearchRequest.getHotelId(), distanceFromSearchRequest.getLocation(),
        distanceFromSearchRequest.getLocationFormat(),
        distanceFromSearchRequest.getRadius(), distanceFromSearchRequest.getRadiusUnit());

    var distanceFromSearchRequestDto = hotelLocationRequestMapper.toDto(
        distanceFromSearchRequest);

    var hotelsWithDistance = getHotelsByLocation(distanceFromSearchRequestDto);

    return hotelLocationResponseMapper.toModel(findHotelLocationByCode(hotelsWithDistance,
        distanceFromSearchRequestDto.getHotelId()));
  }

  @Override
  public List<DistanceFromSearchResponse> getHotelDistancesFromSearch(
      DistanceFromSearchRequest distanceFromSearchRequest) {
    log.debug(
        "Entered getHotelDistancesFromSearch with hotelId={}, location={}, locationFormat={},"
            + " radius={}, radiusUnit={}",
        distanceFromSearchRequest.getHotelId(), distanceFromSearchRequest.getLocation(),
        distanceFromSearchRequest.getLocationFormat(),
        distanceFromSearchRequest.getRadius(), distanceFromSearchRequest.getRadiusUnit());

    var distanceFromSearchRequestDto = hotelLocationRequestMapper.toDto(
        distanceFromSearchRequest);

    var hotelsWithDistance = getHotelsByLocation(distanceFromSearchRequestDto);
    // eliminating found duplicate hotels
    var distinctHotelsWithDistance = hotelsWithDistance.stream().distinct().toList();
    return hotelLocationResponseMapper.toModels(distinctHotelsWithDistance);
  }

  private List<HotelLocationResponse> getHotelsByLocation(
      HotelLocationRequest distanceFromSearchRequestDto) {
    log.trace("Getting hotels by location by locationFormat={}",
        distanceFromSearchRequestDto.getLocationFormat());
    var locationFormat = LocationFormat.retrieveLocationFormat(
        distanceFromSearchRequestDto.getLocationFormat());

    return switch (locationFormat) {
      case LAT_LONG -> snowDropClient.getHotelsLocationByLatLong(
          distanceFromSearchRequestDto, getLatLongLocation(
              distanceFromSearchRequestDto.getLocation()));
      case PLACE_ID, MANAGED_PLACE_ID -> snowDropClient.getHotelsLocationByPlaceId(
          distanceFromSearchRequestDto, locationFormat.getValue()
      );
    };
  }

  private List<String> getLatLongLocation(String location) {
    var latlong = Arrays.asList(location.split(","));
    if (latlong.size() != 2) {
      var message = String.format("Error while trying to get lat long location for location=%s",
              location);
      var exception = new HotelDistanceException(ErrorCode.DIGITAL_INVALID_LOCATION_2_EXCEPTION,
              message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return latlong;
  }

  private HotelLocationResponse findHotelLocationByCode(
      List<HotelLocationResponse> hotelLocationResponseList,
      String code) {
    return hotelLocationResponseList
        .stream()
        .filter(hotelLocationResponse -> hotelLocationResponse.getCode().equals(code))
        .findFirst()
        .orElseThrow(() -> {
          var message = "Hotel with location was not found!";
          var exception = new SnowDropException(ErrorCode.DIGITAL_NO_LOCATION_FOUND_EXCEPTION,
                  message);
          ExceptionLogger.log(log, exception);
          return exception;
        });
  }
}
