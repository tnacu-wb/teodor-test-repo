package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.SearchBookingsResponseDto;
import uk.co.whitbread.reservation.domain.model.out.Booker;
import uk.co.whitbread.reservation.domain.model.out.BookingStatus;
import uk.co.whitbread.reservation.domain.model.out.SearchBooking;
import uk.co.whitbread.reservation.domain.model.out.SearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.StayingGuest;

@Mapper(componentModel = "spring", uses = {ReservationOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SearchBookingsResponseOhipMapper {

  String CANCELLED_BOOKING_STATUS = "Cancelled";
  String RESERVED_BOOKING_STATUS = "Reserved";

  default SearchBookingsResponse toModel(SearchBookingsResponseDto searchBookingsResponseDto) {
    final var searchBookings = toSearchBookingsForModel(searchBookingsResponseDto);

    return SearchBookingsResponse.builder().bookings(searchBookings)
        .hasMore(searchBookingsResponseDto.getHasMore())
        .totalPages(searchBookingsResponseDto.getTotalPages())
        .limit(searchBookingsResponseDto.getLimit())
        .offset(searchBookingsResponseDto.getOffset())
        .totalResults(searchBookingsResponseDto.getTotalResults())
        .responseLimitExceeded(searchBookingsResponseDto.getResponseLimitExceeded())
        .build();
  }

  default List<SearchBooking> toSearchBookingsForModel(
      SearchBookingsResponseDto searchBookingsResponseDto) {

    if (searchBookingsResponseDto.getBookings() == null) {
      return new ArrayList<>();
    }

    return searchBookingsResponseDto.getBookings().stream().map(searchBookingDto -> {

      var searchBooking = SearchBooking.builder()
          .bookingReference(searchBookingDto.getBookingReference())
          .hotelId(searchBookingDto.getHotelId())
          .hotelName(searchBookingDto.getHotelName())
          .arrivalDate(searchBookingDto.getArrivalDate())
          .departureDate(searchBookingDto.getDepartureDate())
          .totalCost(
              BigDecimal.ZERO) // Opera does not provide total cost for search operation. Not shown in UI.
          .currencyCode(
              "GBP") // Opera does not provide currency for search operation. Not shown in UI.
          .build();

      if (searchBookingDto.getBooker() != null) {
        searchBooking.setBooker(Booker.builder()
            .title(searchBookingDto.getBooker().getTitle())
            .firstName(searchBookingDto.getBooker().getFirstName())
            .lastName(searchBookingDto.getBooker().getLastName())
            .build());
      }

      if (searchBookingDto.getReservations() != null) {
        searchBooking.setStayingGuests(
            searchBookingDto.getReservations().stream()
                .map(reservation -> StayingGuest.builder()
                    .title(reservation.getStayingGuest().getTitle())
                    .firstName(reservation.getStayingGuest().getFirstName())
                    .lastName(reservation.getStayingGuest().getLastName())
                    .build()).toList());
      }

      if (CANCELLED_BOOKING_STATUS.equals(searchBookingDto.getStatus())) {
        searchBooking.setStatus(BookingStatus.CANCELLED.name());
      } else if (RESERVED_BOOKING_STATUS.equals(searchBookingDto.getStatus())) {
        if (searchBookingDto.getDepartureDate().isBefore(LocalDate.now())
            || searchBookingDto.getDepartureDate()
            .equals(LocalDate.now())) {
          searchBooking.setStatus(BookingStatus.PAST.name());
        } else if (searchBookingDto.getArrivalDate().isBefore(LocalDate.now())
            && searchBookingDto.getDepartureDate()
            .isAfter(LocalDate.now())) {
          //temp solution for ongoing status
          searchBooking.setStatus("");
        } else {
          searchBooking.setStatus(BookingStatus.UPCOMING.name());
        }
      } else {
        searchBooking.setStatus(BookingStatus.UNDEFINED.name());
      }

      searchBooking.setSourcePms("OPERA");

      return searchBooking;
    }).toList();
  }

}
