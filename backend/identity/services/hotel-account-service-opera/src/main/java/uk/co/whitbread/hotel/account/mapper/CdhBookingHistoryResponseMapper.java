package uk.co.whitbread.hotel.account.mapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.BookerDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GuestsDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.ResultsDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.RoomsDto;
import uk.co.whitbread.hotel.account.model.BookingStatus;
import uk.co.whitbread.hotel.account.model.PersonDetails;
import uk.co.whitbread.hotel.account.model.RoomTypes;
import uk.co.whitbread.hotel.account.model.Stay;
import uk.co.whitbread.hotel.account.model.StaysResponse;

@Mapper(componentModel = "spring")
public interface CdhBookingHistoryResponseMapper extends CommonMapper {

  @Mapping(target = "totalSize", source = "totalResults")
  @Mapping(target = "pageSize", source = "searchResults")
  @Mapping(target = "stays", source = "results")
  StaysResponse toCdhBookingHistoryResponse(CdhReservationSearchDto cdhReservationSearchDto);

  @Mapping(target = "noOfRooms", expression = "java(results.getRooms().size())")
  @Mapping(target = "roomTypes", source = "rooms")
  @Mapping(target = "confirmationNumber", source = "bookingReference")
  @Mapping(target = "cancelled", source = "status", qualifiedByName = "cancelled")
  @Mapping(target = "checkedIn", source = "status", qualifiedByName = "checkedIn")
  @Mapping(target = "bookingStatus", source = "results", qualifiedByName = "bookingStatus")
  @Mapping(target = "guestHistoryNumber", source = "bartGuestHistoryNumber")
  @Mapping(target = "bookedBy", source = "booker", qualifiedByName = "bookedBy")
  @Mapping(target = "rateName", source = "rateCategory")
  @Mapping(target = "leadGuest", source = "rooms", qualifiedByName = "stayLeadGuest")
  @Mapping(target = "leadGuestSurname", source = "rooms", qualifiedByName = "stayLeadGuestLastName")
  @Mapping(target = "arrivalDate", source = "arrivalDate", qualifiedByName = "localDate")
  @Mapping(target = "departureDate", source = "departureDate", qualifiedByName = "localDate")
  @Mapping(target = "bookingDate", source = "bookingDate", qualifiedByName = "localDate")
  Stay toStay(ResultsDto results);

  @Mapping(target = "carDataPresent", source = "carDetails")
  @Mapping(target = "personDetails", source = "guests", qualifiedByName = "personDetails")
  @Mapping(target = "leadGuest", source = "guests", qualifiedByName = "roomLeadGuest")
  @Mapping(target = "bookingStatus", source = "status")
  RoomTypes toRoomTypes(RoomsDto room);

  @Named("personDetails")
  default PersonDetails toPersonDetails(List<GuestsDto> guests) {
    if (guests == null || guests.isEmpty()) {
      return PersonDetails.builder().build();
    }
    GuestsDto leadGuest = findLeadGuest(guests);
    return PersonDetails.builder()
        .title(leadGuest.getTitle())
        .firstName(leadGuest.getFirstName())
        .lastName(leadGuest.getLastName())
        .build();
  }

  @Named("localDate")
  default LocalDate toLocalDate(String date) {
    return OffsetDateTime.parse(date).toLocalDate();
  }

  @Named("cancelled")
  default boolean toCancelled(String bookingStatus) {
    return "CANCELLED".equalsIgnoreCase(bookingStatus);
  }

  @Named("checkedIn")
  default boolean toCheckedIn(String bookingStatus) {
    return "ARRIVED".equalsIgnoreCase(bookingStatus) ||
        "CHECKEDIN".equalsIgnoreCase(bookingStatus);
  }

  @Named("bookingStatus")
  default BookingStatus toBookingStatus(ResultsDto results) {
    var bookingStatus = results.getStatus();
    return mapBookingStatus(bookingStatus, results.getDepartureDate());
  }

  @Named("bookedBy")
  default String toBookedBy(BookerDto booker) {
    if (Objects.isNull(booker)) {
      return StringUtils.EMPTY;
    }
    return String.join(" ", booker.getFirstName(), booker.getLastName());
  }

  @Named("stayLeadGuest")
  default String toStayLeadGuest(List<RoomsDto> rooms) {
    return getRoomLeadGuestFullName(rooms);
  }

  @Named("stayLeadGuestLastName")
  default String toStayLeadGuestLastName(List<RoomsDto> rooms) {
    return getLeadGuestLastName(rooms);
  }

  @Named("roomLeadGuest")
  default String toRoomLeadGuest(List<GuestsDto> guests) {
    return getLeadGuestFullName(guests);
  }

  default List<GuestsDto> getGuestList(List<RoomsDto> rooms) {
    return rooms.isEmpty() ? new ArrayList<>() :
        rooms.stream()
            .filter(room -> !room.getStatus().equals(CANCELLED))
            .findFirst()
            .orElse(rooms.get(0))
            .getGuests();
  }

  default GuestsDto findLeadGuest(List<GuestsDto> guests) {
    var leadGuestOptional = guests.stream()
        .filter(GuestsDto::getLeadGuest)
        .findFirst();
    return leadGuestOptional.orElseGet(() -> guests.get(0));
  }

  default String getRoomLeadGuestFullName(List<RoomsDto> rooms) {
    if (rooms == null || rooms.isEmpty()) {
      return StringUtils.EMPTY;
    }
    List<GuestsDto> guests = getGuestList(rooms);
    if (guests == null || guests.isEmpty()) {
      return StringUtils.EMPTY;
    }
    GuestsDto leadGuest = findLeadGuest(guests);
    return String.join(" ", leadGuest.getTitle(), leadGuest.getFirstName(), leadGuest.getLastName());
  }

  default String getLeadGuestLastName(List<RoomsDto> rooms) {
    if (rooms == null || rooms.isEmpty()) {
      return StringUtils.EMPTY;
    }
    List<GuestsDto> guests = getGuestList(rooms);
    if (guests == null || guests.isEmpty()) {
      return StringUtils.EMPTY;
    }
    GuestsDto leadGuest = findLeadGuest(guests);
    return leadGuest.getLastName();
  }

  default String getLeadGuestFullName(List<GuestsDto> guests) {
    if (guests == null || guests.isEmpty()) {
      return StringUtils.EMPTY;
    }
    GuestsDto leadGuest = findLeadGuest(guests);
    return String.join(" ", leadGuest.getTitle(), leadGuest.getFirstName(), leadGuest.getLastName());
  }
}
