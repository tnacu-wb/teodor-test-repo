package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import java.util.Arrays;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilitySearchRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {
    Arrays.class})
public interface AvailabilityRequestMapper {

  @Mapping(target = "roomStayQuantity", source = "numberOfRooms")
  @Mapping(target = "roomStayStartDate", source = "arrivalDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "roomStayEndDate", source = "departureDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "children", expression = "java(Arrays.asList(availabilitySearch.getChildren()))")
  @Mapping(target = "adults", expression = "java(Arrays.asList(availabilitySearch.getAdults()))")
  @Mapping(target = "roomTypes", expression = "java(Arrays.asList(availabilitySearch.getRoomType()))")
  AvailabilityRequestDto toAvailabilityRequestDto(
      AvailabilityRoomSearchCriteria availabilitySearch);

  @Mapping(target = "roomStayStartDate", source = "arrivalDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "roomStayEndDate", source = "departureDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "roomStayQuantity", source = "numberOfRooms")
  AvailabilityRequestDto toAvailabilityRequestDto(
      AvailabilitySearchRequest hotelAvailabilitySearchRequest);

  AvailabilityRequestDto toHotelAvailabilityRequestDto(
      AvailabilityRequest availabilityRequest);
}
