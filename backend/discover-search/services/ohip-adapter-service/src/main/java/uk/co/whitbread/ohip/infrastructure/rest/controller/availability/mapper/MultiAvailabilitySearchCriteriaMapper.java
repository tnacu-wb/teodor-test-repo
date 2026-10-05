package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import java.util.Arrays;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequestV2;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.MultiAvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.MultiAvailabilityRequestV2Dto;

@Mapper(componentModel = "spring", imports = {Arrays.class})
public interface MultiAvailabilitySearchCriteriaMapper {

  @Mapping(target = "hotelIds", expression = "java(Arrays.asList(multiAvailabilityRequestDto.getHotelIds()))")
  @Mapping(target = "numberOfRooms", expression = "java(Arrays.asList(multiAvailabilityRequestDto.getNumberOfRooms()))")
  @Mapping(target = "roomTypes", expression = "java(Arrays.asList(multiAvailabilityRequestDto.getRoomTypes()))")
  @Mapping(target = "adults", expression = "java(Arrays.asList(multiAvailabilityRequestDto.getAdults()))")
  @Mapping(target = "children", expression = "java(Arrays.asList(multiAvailabilityRequestDto.getChildren()))")
  @Mapping(target = "cotsRequired", expression = "java(Arrays.asList(multiAvailabilityRequestDto.getCotsRequired()))")
  MultiHotelAvailabilityRequest toRequestModel(MultiAvailabilityRequestDto multiAvailabilityRequestDto);

  MultiHotelAvailabilityRequestV2 toRequestV2Model(
      MultiAvailabilityRequestV2Dto multiAvailabilityRequestDto);
}
