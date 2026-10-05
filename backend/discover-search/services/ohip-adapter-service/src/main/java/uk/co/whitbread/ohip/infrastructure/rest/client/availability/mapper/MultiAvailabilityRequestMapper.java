package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import java.util.Arrays;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.HotelAvailability;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.HotelAvailabilityStatus;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyRoomStayType;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequestV2;
import uk.co.whitbread.ohip.domain.model.availability.in.Room;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResultV2;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {
    Arrays.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {MultiAvailabilityResultMapper.class})
public interface MultiAvailabilityRequestMapper {

  @Mapping(target = "roomStayStartDate", source = "arrivalDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "roomStayEndDate", source = "departureDate", dateFormat = "yyyy-MM-dd")
  MultiHotelAvailabilityRequestDto toRequestDto(MultiHotelAvailabilityRequest availabilityRequest);

  @Mapping(target = "hotelAvailabilityResults", source = "hotelAvailability")
  MultiAvailabilityResult toResultModel(HotelAvailability availabilityResponse);

  MultiHotelAvailabilityRequestV2Dto toRequestV2Dto(MultiHotelAvailabilityRequestV2 multiHotelAvailabilityRequest);

  @Mapping(target = "hotelAvailabilityResults",
      source = "multiHotelAvailabilityRequestV2", qualifiedByName = "resultMapper")
  MultiAvailabilityResultV2 toResultV2Model(SearchPropertyResponseType multiHotelAvailabilityRequestV2);

  @Named("resultMapper")
  default List<HotelAvailabilityResultV2> toResultForModel(SearchPropertyResponseType multiHotelAvailabilityRequestV2) {
    return multiHotelAvailabilityRequestV2.getRoomStays()
        .stream()
        .map(r -> mapHotelAvaResult(r))
        .toList();
  }

  @Mapping(target = "numberOfUnits", source = "numberOfRooms")
  RoomDto toRoomDtoModel(
      Room room);

  private HotelAvailabilityResultV2 mapHotelAvaResult(SearchPropertyRoomStayType roomStayType) {
    return HotelAvailabilityResultV2.builder()
        .hotelId(roomStayType.getPropertyInfo().getHotelCode())
        .available(isHotelAvailable(roomStayType.getAvailability()))
        .minimumRate(roomStayType.getMinimumRate().getAmountAfterTax())
        .currency(roomStayType.getMinimumRate().getCurrencyCode())
        .build();
  }

  private Boolean isHotelAvailable(HotelAvailabilityStatus availability) {
    switch (availability) {
      case AVAILABLEFORSALE -> {
        return true;
      }
      default -> {
        return false;
      }
    }
  }

}
