package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.SearchBookingsRequestDto;

@Mapper(componentModel = "spring")
public interface SearchBookingsRequestMapper {

  BookingSearchCriteria toBookingSearchCriteriaModel(SearchBookingsRequestDto searchBookingsRequestDto);

}
