package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;


import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.Booker;
import uk.co.whitbread.reservation.domain.model.out.SearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.StayingGuest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.SearchBookingBookerDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.SearchBookingDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.SearchBookingStayingGuestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.SearchBookingsResponseDto;

@Mapper(componentModel = "spring")
public interface SearchBookingsResponseMapper {

  @Mapping(expression = "java(toSearchBookingsDto(searchBookingsResponse))", target = "bookings")
  SearchBookingsResponseDto toDto(SearchBookingsResponse searchBookingsResponse);

  default List<SearchBookingDto> toSearchBookingsDto(
      SearchBookingsResponse searchBookingsResponse) {

    if (searchBookingsResponse.getBookings() == null) {
      return new ArrayList<>();
    }

    return searchBookingsResponse.getBookings().stream().map(
        searchBooking -> SearchBookingDto.builder().arrivalDate(searchBooking.getArrivalDate())
            .departureDate(searchBooking.getDepartureDate()).bookingReference(searchBooking.getBookingReference())
            .hotelId(searchBooking.getHotelId()).hotelName(searchBooking.getHotelName())
            .totalCost(searchBooking.getTotalCost()).currencyCode(searchBooking.getCurrencyCode())
            .sourcePms(searchBooking.getSourcePms()).status(searchBooking.getStatus())
            .booker(toBookerDto(searchBooking.getBooker()))
            .stayingGuests(toStayingGuestDto(searchBooking.getStayingGuests())).build()).toList();

  }

  default List<SearchBookingStayingGuestDto> toStayingGuestDto(List<StayingGuest> stayingGuests) {

    if (stayingGuests == null) {
      return new ArrayList<>();
    }

    return stayingGuests.stream().map(
            stayingGuest -> SearchBookingStayingGuestDto.builder().title(stayingGuest.getTitle())
                .firstName(stayingGuest.getFirstName()).lastName(stayingGuest.getLastName()).build())
        .toList();
  }

  default SearchBookingBookerDto toBookerDto(Booker booker) {
    if (booker == null) {
      return null;
    }

    return SearchBookingBookerDto.builder().title(booker.getTitle())
        .firstName(booker.getFirstName())
        .lastName(booker.getLastName()).build();
  }
}
