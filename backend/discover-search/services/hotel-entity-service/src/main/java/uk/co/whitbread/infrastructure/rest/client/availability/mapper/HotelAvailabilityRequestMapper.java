package uk.co.whitbread.infrastructure.rest.client.availability.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.in.MultiHotelAvaSearchCriteria;
import uk.co.whitbread.domain.model.availability.in.Room;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookingChannelDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityRequestV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomV2Dto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityByIdsRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityByIdsRequestOhipV2Dto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityByIdsRequestOhipV3Dto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.MultiHotelAvaSearchCriteriaOhip;

@Mapper(componentModel = "spring")
public interface HotelAvailabilityRequestMapper {

  HotelAvailabilityRequestOhipDto toOhipDto(HotelAvailabilityRequest hotelAvailabilityRequest);

  HotelAvailabilityByIdsRequestOhipDto toAvailabilityByIdsOhipModel(
      HotelAvailabilityByIdsRequest hotelAvailabilityByIdsRequest);

  MultiHotelAvaSearchCriteriaOhip toMultiAvaOhipModel(
      MultiHotelAvaSearchCriteria multiHotelAvaSearchCriteria);

  @Mapping(target = "numberOfRooms", source = "hotelAvailabilitiesRequest", qualifiedByName = "mapRoomsNumber")
  @Mapping(target = "adults", source = "adultsNumber")
  @Mapping(target = "children", source = "childrenNumber")
  @Mapping(target = "cotsRequired", source = "hotelAvailabilitiesRequest", qualifiedByName = "mapCotRequired")
  MultiHotelAvaSearchCriteriaOhip toMultiAvaSearch(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest);

  @Mapping(target = "rooms", source = "hotelAvailabilitiesRequest", qualifiedByName = "mapOperaRooms")
  @Mapping(target = "bookingChannel", source = "hotelAvailabilitiesRequest", qualifiedByName = "mapBookingChannel")
  @Mapping(target = "accountId", source = "companyId")
  MultiAvailabilityRequestV2Dto toNewMultiAvaSearch(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest);

  @Named("mapRoomsNumber")
  default List<Integer> mapRoomsNumber(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    LinkedList<Integer> countOfRoomType = new LinkedList<>();
    for (String roomType : hotelAvailabilitiesRequest.getRoomTypes()) {
      countOfRoomType.add(1);
    }
    return countOfRoomType.stream().toList();
  }

  @Named("mapCotRequired")
  default List<Boolean> mapCotRequired(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return new ArrayList<>(
        Collections.nCopies(hotelAvailabilitiesRequest.getRoomTypes().size(), false));
  }

  @Mapping(target = "rates.corporateRates", source = "rates.corporateRates", qualifiedByName = "mapCorporateRate")
  HotelAvailabilityByIdsRequestOhipV2Dto toAvailabilityByIdsOhipV2Model(HotelAvailabilityByIdsV2Request request);

  default uk.co.whitbread.infrastructure.rest.client.availability.model.RoomV2Dto roomToRoomV2Dto(Room room) {
    if (room == null) {
      return null;
    }
    uk.co.whitbread.infrastructure.rest.client.availability.model.RoomV2Dto.RoomV2DtoBuilder
        roomV2Dto = uk.co.whitbread.infrastructure.rest.client.availability.model.RoomV2Dto.builder();
    roomV2Dto.tag(room.getTag());
    roomV2Dto.adults(room.getAdults());
    roomV2Dto.children(room.getChildren());
    roomV2Dto.numberOfRooms(room.getNumberOfRooms());
    roomV2Dto.roomTypes(Arrays.asList(room.getRoomType()));
    return roomV2Dto.build();
  }

  @Named("mapBookingChannel")
  default BookingChannelDto mapBookingChannel(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    BookingChannelDto bookingChannelDto = new BookingChannelDto();
    bookingChannelDto.setChannel(hotelAvailabilitiesRequest.getChannel());
    bookingChannelDto.setSubchannel(hotelAvailabilitiesRequest.getSubChannel());
    bookingChannelDto.setLanguage(hotelAvailabilitiesRequest.getLanguage());
    return bookingChannelDto;
  }

  @Named("mapOperaRooms")
  default List<RoomV2Dto> mapOperaRooms(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    LinkedList<RoomV2Dto> roomsV2Dto = new LinkedList<>();
    for (int i = 0; i < hotelAvailabilitiesRequest.getRoomTypes().size(); i++) {
      RoomV2Dto roomV2Dto = new RoomV2Dto();
      roomV2Dto.setTag(hotelAvailabilitiesRequest.getRoomTypes().get(i));
      roomV2Dto.setAdults(hotelAvailabilitiesRequest.getAdultsNumber().get(i));
      roomV2Dto.setChildren(hotelAvailabilitiesRequest.getChildrenNumber().get(i));
      roomsV2Dto.add(roomV2Dto);
    }
    return updateNumberOfRooms(roomsV2Dto).stream().distinct().toList();
  }

  private LinkedList<RoomV2Dto> updateNumberOfRooms(LinkedList<RoomV2Dto> roomsV2Dto) {
    //Calculate the number of rooms for each
    // roomtype and occupancy (i,e number of adults & children)
    final Map<String, Map<Pair<Integer, Integer>, Long>> roomFrequencyPerOccupancy =
        roomsV2Dto.stream()
            .collect(
                Collectors.groupingBy(RoomV2Dto::getTag,
                    Collectors.groupingBy(e -> Pair.of(e.getAdults(), e.getChildren()),
                        Collectors.counting())));

    for (final RoomV2Dto room : roomsV2Dto) {
      final Pair<Integer, Integer> occupancy =
          Pair.of(room.getAdults(), room.getChildren());
      final Map<Pair<Integer, Integer>, Long> numberOfRooms =
          roomFrequencyPerOccupancy.get(room.getTag());
      if (numberOfRooms.containsKey(occupancy)) {
        room.setNumberOfRooms(numberOfRooms.getOrDefault(occupancy, Long.valueOf(0)).intValue());
      }
    }
    return roomsV2Dto;
  }


  @Named("mapCorporateRate")
  default CorporateRate mapCorporateRate(List<CorporateRate> rates) {
    if (rates == null || rates.isEmpty()) {
      return null;
    }
    return rates.get(0); // assuming the list has only one item

  }

  HotelAvailabilityByIdsRequestOhipV3Dto toAvailabilityByIdsOhipV3Model(HotelAvailabilityByIdsV2Request request);
}
