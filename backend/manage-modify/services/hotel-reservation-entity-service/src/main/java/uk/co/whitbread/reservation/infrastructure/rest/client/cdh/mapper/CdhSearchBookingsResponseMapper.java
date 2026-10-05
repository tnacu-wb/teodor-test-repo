package uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.RoomsDto;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.model.out.CdhBooker;
import uk.co.whitbread.reservation.domain.model.out.CdhGuests;
import uk.co.whitbread.reservation.domain.model.out.CdhResults;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.Rooms;

@Mapper(componentModel = "spring")
public interface CdhSearchBookingsResponseMapper {

  static final Integer PAGE_SIZE = 50;

  @Mapping(source = "totalResults", target = "cdhSearchResults")
  @Mapping(source = "totalSize", target = "pageResults")
  default CdhSearchBookingsResponse toModel(CdhReservationSearchDto cdhReservationSearchDto, Integer pageSize,
                                            Integer pageNumber, String bookerLastName, String guestLastName) {

    if (cdhReservationSearchDto == null
            || cdhReservationSearchDto.getTotalResults() == null
            || cdhReservationSearchDto.getTotalResults() > PAGE_SIZE) {

      return CdhSearchBookingsResponse.builder()
              .results(new ArrayList<>())
              .cdhSearchResults(Optional.ofNullable(cdhReservationSearchDto)
                      .map(CdhReservationSearchDto::getTotalResults).orElse(0))
              .searchResults(0)
              .pageResults(0)
              .hasMore(false)
              .responseLimitExceeded(true)
              .build();
    }

    var cdhResults =  toCdhResultsModel(cdhReservationSearchDto);

    cdhResults = toFilterByBookerOrGuestLastnameModel(bookerLastName, guestLastName, cdhResults);

    return CdhSearchBookingsResponse.builder()
            .results(cdhResults)
            .cdhSearchResults(cdhReservationSearchDto.getTotalResults())
            .searchResults(cdhReservationSearchDto.getSearchResults())
            .pageResults(cdhReservationSearchDto.getTotalSize())
            .hasMore((pageSize * pageNumber) < cdhReservationSearchDto.getTotalResults())
            .responseLimitExceeded(false)
            .build();

  }

  default List<CdhResults> toCdhResultsModel(CdhReservationSearchDto cdhReservationSearchDto) {

    if (cdhReservationSearchDto.getResults() == null) {
      return Collections.emptyList();
    }
    return cdhReservationSearchDto.getResults().stream()
            .map(cdhResultsDto -> {
              var cdhResults = CdhResults.builder()
                      .bookingReference(cdhResultsDto.getBookingReference())
                      .status(toSetStatusModel(cdhResultsDto.getStatus(), cdhResultsDto.getDepartureDate(),
                      cdhResultsDto.getArrivalDate()))
                      .sourceSystem(cdhResultsDto.getSourceSystem())
                      .hotelId(cdhResultsDto.getHotelCode())
                      .hotelName(cdhResultsDto.getHotelName())
                      .arrivalDate(getLocalDateFromString(cdhResultsDto.getArrivalDate()))
                      .departureDate(getLocalDateFromString(cdhResultsDto.getDepartureDate()))
                      .cancellationDate(cdhResultsDto.getCancellationDate() != null
                          ? getLocalDateFromString(cdhResultsDto.getCancellationDate()) : null)
                      .totalCost(Optional.ofNullable(cdhResultsDto.getTotalCost()).isPresent()
                          ? cdhResultsDto.getTotalCost().getAmount() : null)
                      .currencyCode(Optional.ofNullable(cdhResultsDto.getTotalCost()).isPresent()
                          ? cdhResultsDto.getTotalCost().getCurrency() : "")
                      .build();

              if (cdhResultsDto.getBooker() != null) {
                cdhResults.setBooker(CdhBooker.builder()
                        .title(cdhResultsDto.getBooker().getTitle())
                        .firstName(cdhResultsDto.getBooker().getFirstName())
                        .lastName(cdhResultsDto.getBooker().getLastName())
                        .build());
              }

              if (cdhResultsDto.getRooms() != null) {
                var reservationId = cdhResultsDto.getRooms().stream()
                    .map(RoomsDto::getReservationId).toList().get(0);
                cdhResults.setRooms(Collections.singletonList(
                    Rooms.builder().reservationId(reservationId).build()));
                var guests = cdhResultsDto.getRooms().stream()
                                .flatMap(roomsDto -> roomsDto.getGuests()
                                        .stream()).toList();
                guests.forEach(guestsDto -> {
                  cdhResults.setGuests(Collections.singletonList(CdhGuests.builder()
                      .title(guestsDto.getTitle())
                      .firstName(guestsDto.getFirstName())
                      .lastName(guestsDto.getLastName())
                      .build()));
                });
              }
              return cdhResults;
            }).toList();
  }

  private LocalDate getLocalDateFromString(String stringDate) {
    ZonedDateTime result = ZonedDateTime.parse(stringDate, DateTimeFormatter.ISO_DATE_TIME);
    return result.toLocalDate();
  }

  public default List<CdhResults> toFilterByBookerOrGuestLastnameModel(String bookerLastName, String guestLastName,
                                                                List<CdhResults> cdhResults) {

    if (bookerLastName != null && !bookerLastName.isEmpty()) {

      return cdhResults.stream()
              .filter(Objects::nonNull)
              .filter(results -> bookerLastName.equalsIgnoreCase(results.getBooker().getLastName()))
              .toList();
    } else if (guestLastName != null && !guestLastName.isEmpty()) {

      return cdhResults.stream()
              .filter(Objects::nonNull)
              .filter(cdhResult -> cdhResult.getGuests().stream()
                      .anyMatch(cdhGuest -> guestLastName.equalsIgnoreCase(cdhGuest.getLastName())))
              .toList();
    }
    return cdhResults;
  }

  private String toSetStatusModel(String status, String departureDate, String arrivalDate) {

    var departureDateInLocalDate = LocalDate.parse(departureDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    var arrivalDateInLocalDate = LocalDate.parse(arrivalDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    switch (status.toUpperCase()) {
      case HotelReservationConstants.RESERVED_BOOKING_STATUS:
        if (departureDateInLocalDate.isBefore(LocalDate.now()) || departureDateInLocalDate.isEqual(LocalDate.now())) {
          status = HotelReservationConstants.PAST_BOOKING_STATUS;
        } else if (arrivalDateInLocalDate.isBefore(LocalDate.now())) {
          status = HotelReservationConstants.CHECKED_IN_BOOKING_STATUS;
        } else if (arrivalDateInLocalDate.isAfter(LocalDate.now())) {
          status = HotelReservationConstants.UPCOMING_BOOKING_STATUS;
        } else {
          status = HotelReservationConstants.UPCOMING_BOOKING_STATUS;
        }

        break;
      case HotelReservationConstants.CANCELLED_BOOKING_STATUS:
        status = "Cancelled";
        break;
      case HotelReservationConstants.NO_SHOW, HotelReservationConstants.PAST_BOOK_STATUS,
           HotelReservationConstants.CHECKED_OUT_STATUS, HotelReservationConstants.RELEASED_BOOKING_STATUS:
        status = HotelReservationConstants.PAST_BOOKING_STATUS;
        break;
      case HotelReservationConstants.INHOUSE_BOOKING_STATUS, HotelReservationConstants.DUEOUT_BOOKING_STATUS,
           HotelReservationConstants.PENDINGCHECKOUT_BOOKING_STATUS, HotelReservationConstants.CHECKEDIN_BOOKING_STATUS,
           HotelReservationConstants.ARRIVED_BOOKING_STATUS:
        status = HotelReservationConstants.CHECKED_IN_BOOKING_STATUS;
        break;
      case HotelReservationConstants.DUEIN_BOOKING_STATUS, HotelReservationConstants.FUTURE_BOOK_STATUS,
           HotelReservationConstants.PREPAID_BOOKING_STATUS, HotelReservationConstants.REQUESTED_BOOKING_STATUS,
           HotelReservationConstants.WAITLISTED_BOOKING_STATUS, HotelReservationConstants.UNARRIVED_BOOKING_STATUS:
        status = HotelReservationConstants.UPCOMING_BOOKING_STATUS;
        break;
      default:
        status = HotelReservationConstants.UNDEFINED_BOOKING_STATUS;
    }

    return status;
  }
}
