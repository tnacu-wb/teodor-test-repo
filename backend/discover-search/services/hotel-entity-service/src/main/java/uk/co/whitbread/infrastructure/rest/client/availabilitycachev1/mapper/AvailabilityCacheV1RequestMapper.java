package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.Room;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.in.AvailabilityCacheRequestV1;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AvailabilityCacheV1RequestMapper {

  @Mapping(target = "arrival", source = "arrivalDate")
  @Mapping(target = "departure", source = "departureDate")
  @Mapping(target = "adults", source = "adultsNumber")
  @Mapping(target = "children", source = "childrenNumber")
  @Mapping(target = "cot", source = "hotelAvailabilitiesRequest", qualifiedByName = "mapCotRequired")
  @Mapping(target = "rooms", source = "hotelAvailabilitiesRequest", qualifiedByName = "mapRoomsNumber")
  AvailabilityCacheRequestV1 toDto(HotelAvailabilitiesRequest hotelAvailabilitiesRequest);

  @Mapping(target = "hotelCodes", source = "hotelIds")
  @Mapping(target = "arrival", source = "arrivalDate")
  @Mapping(target = "departure", source = "departureDate")
  @Mapping(target = "adults", source = "adultsNumber")
  @Mapping(target = "children", source = "childrenNumber")
  @Mapping(target = "cot", source = "cotsRequired")
  @Mapping(target = "rooms", source = "hotelAvailabilityByIdsRequest", qualifiedByName = "mapRoomsNumberByIds")
  AvailabilityCacheRequestV1 toDtoByIds(HotelAvailabilityByIdsRequest hotelAvailabilityByIdsRequest);

  @Mapping(target = "hotelCodes", source = "hotelIds")
  @Mapping(target = "arrival", source = "arrivalDate")
  @Mapping(target = "departure", source = "departureDate")
  @Mapping(target = "adults", source = "hotelAvailabilityByIdsV2Request", qualifiedByName = "mapAdultsNumberByIds")
  @Mapping(target = "children", source = "hotelAvailabilityByIdsV2Request", qualifiedByName = "mapChildrenNumberByIds")
  @Mapping(target = "rooms", source = "hotelAvailabilityByIdsV2Request", qualifiedByName = "mapRoomsNumberByIdsV2")
  @Mapping(target = "roomQty", source = "hotelAvailabilityByIdsV2Request", qualifiedByName = "mapRoomQtyByIdsV2")
  @Mapping(target = "cot", source = "hotelAvailabilityByIdsV2Request", qualifiedByName = "mapCotRequiredByIdsV2")
  AvailabilityCacheRequestV1 toDtoByIdsV2(HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsV2Request);

  @Named("mapCotRequired")
  default List<Boolean> mapCotRequired(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return new ArrayList<>(
        Collections.nCopies(hotelAvailabilitiesRequest.getRoomTypes().size(), false));
  }

  @Named("mapRoomsNumber")
  default Integer mapRoomsNumber(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return hotelAvailabilitiesRequest.getRoomTypes().size();
  }

  @Named("mapRoomsNumberByIds")
  default Integer mapRoomsNumberByIds(HotelAvailabilityByIdsRequest hotelAvailabilityByIdsRequest) {
    return hotelAvailabilityByIdsRequest.getRoomTypes().size();
  }

  @Named("mapAdultsNumberByIds")
  default List<Integer> mapAdultsNumberByIds(HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsV2Request) {
    return hotelAvailabilityByIdsV2Request.getRooms().stream().map(Room::getAdults).toList();
  }

  @Named("mapChildrenNumberByIds")
  default List<Integer> mapChildrenNumberByIds(HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsV2Request) {
    return hotelAvailabilityByIdsV2Request.getRooms().stream().map(Room::getChildren).toList();
  }

  @Named("mapRoomsNumberByIdsV2")
  default Integer mapRoomsNumberByIdsV2(HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsV2Request) {
    return hotelAvailabilityByIdsV2Request.getRooms().size();
  }

  @Named("mapRoomQtyByIdsV2")
  default List<Integer> mapRoomQtyByIdsV2(HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsV2Request) {
    return hotelAvailabilityByIdsV2Request.getRooms().stream().map(Room::getNumberOfRooms).toList();
  }

  @Named("mapCotRequiredByIdsV2")
  default List<Boolean> mapCotRequiredByIdsV2(HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsV2Request) {
    return new ArrayList<>(
        Collections.nCopies(hotelAvailabilityByIdsV2Request.getRooms().size(), false));
  }


}
