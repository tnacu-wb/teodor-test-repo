package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookingChannelDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityRequestV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomV2Dto;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.reservation.domain.model.availability.in.MultiHotelAvaSearchCriteria;
import uk.co.whitbread.reservation.domain.model.availability.in.Room;
import uk.co.whitbread.reservation.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.HotelAvailabilityByIdsRequestOhipDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.HotelAvailabilityByIdsRequestOhipV2Dto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.HotelAvailabilityRequestOhipDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.MultiHotelAvaSearchCriteriaOhip;

@Mapper(componentModel = "spring")
public interface HotelAvailabilityRequestMapper {

  HotelAvailabilityRequestOhipDto toOhipDto(HotelAvailabilityRequest hotelAvailabilityRequest);

  HotelAvailabilityByIdsRequestOhipDto toAvailabilityByIdsOhipModel(
      HotelAvailabilityByIdsRequest hotelAvailabilityByIdsRequest);

  MultiHotelAvaSearchCriteriaOhip toMultiAvaOhipModel(
      MultiHotelAvaSearchCriteria multiHotelAvaSearchCriteria);

  @Mapping(target = "numberOfRooms", source = "hotelAvailabilitiesRequest", qualifiedByName = "toRoomsNumber")
  @Mapping(target = "adults", source = "adultsNumber")
  @Mapping(target = "children", source = "childrenNumber")
  @Mapping(target = "cotsRequired", source = "hotelAvailabilitiesRequest", qualifiedByName = "toCotRequired")
  MultiHotelAvaSearchCriteriaOhip toMultiAvaSearchModel(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest);

  @Mapping(target = "rooms", source = "hotelAvailabilitiesRequest", qualifiedByName = "toOperaRooms")
  @Mapping(target = "bookingChannel", source = "hotelAvailabilitiesRequest", qualifiedByName = "toBookingChannel")
  @Mapping(target = "accountId", source = "companyId")
  MultiAvailabilityRequestV2Dto toNewMultiAvaSearchDto(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest);

  @Named("toRoomsNumber")
  default List<Integer> toRoomsNumberModel(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    LinkedList<Integer> countOfRoomType = new LinkedList<>();
    for (String roomType : hotelAvailabilitiesRequest.getRoomTypes()) {
      countOfRoomType.add(1);
    }
    return countOfRoomType.stream().toList();
  }

  @Named("toCotRequired")
  default List<Boolean> toCotRequiredModel(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return new ArrayList<>(
        Collections.nCopies(hotelAvailabilitiesRequest.getRoomTypes().size(), false));
  }

  HotelAvailabilityByIdsRequestOhipV2Dto toAvailabilityByIdsOhipV2Model(HotelAvailabilityByIdsV2Request request);

  default uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.RoomV2Dto toRoomToRoomV2Dto(Room room) {
    if (room == null) {
      return null;
    }
    uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.RoomV2Dto.RoomV2DtoBuilder
        roomV2Dto = uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.RoomV2Dto.builder();
    roomV2Dto.tag(room.getTag());
    roomV2Dto.adults(room.getAdults());
    roomV2Dto.children(room.getChildren());
    roomV2Dto.numberOfRooms(room.getNumberOfRooms());
    roomV2Dto.roomTypes(Arrays.asList(room.getRoomType()));
    return roomV2Dto.build();
  }

  @Named("toBookingChannel")
  default BookingChannelDto toBookingChannelDto(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    BookingChannelDto bookingChannelDto = new BookingChannelDto();
    bookingChannelDto.setChannel(hotelAvailabilitiesRequest.getChannel());
    bookingChannelDto.setSubchannel(hotelAvailabilitiesRequest.getSubChannel());
    bookingChannelDto.setLanguage(hotelAvailabilitiesRequest.getLanguage());
    return bookingChannelDto;
  }

  @Named("toOperaRooms")
  default List<RoomV2Dto> toOperaRoomsDto(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    LinkedList<RoomV2Dto> roomsV2Dto = new LinkedList<>();
    for (int i = 0; i < hotelAvailabilitiesRequest.getRoomTypes().size(); i++) {
      RoomV2Dto roomV2Dto = new RoomV2Dto();
      roomV2Dto.setTag(hotelAvailabilitiesRequest.getRoomTypes().get(i));
      roomV2Dto.setAdults(hotelAvailabilitiesRequest.getAdultsNumber().get(i));
      roomV2Dto.setChildren(hotelAvailabilitiesRequest.getChildrenNumber().get(i));
      roomV2Dto.setNumberOfRooms(Collections.frequency(hotelAvailabilitiesRequest.getRoomTypes(),
          hotelAvailabilitiesRequest.getRoomTypes().get(i)));
      roomsV2Dto.add(roomV2Dto);
    }

    return roomsV2Dto.stream().distinct().toList();
  }
}
