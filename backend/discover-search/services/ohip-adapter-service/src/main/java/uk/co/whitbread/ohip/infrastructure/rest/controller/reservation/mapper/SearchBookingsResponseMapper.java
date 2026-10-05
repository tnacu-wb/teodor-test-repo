package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.SearchBookingsResponseDto;

@Mapper(componentModel = "spring")
public interface SearchBookingsResponseMapper {

  SearchBookingsResponseDto toSearchBookingsResponseDto(SearchBookingsResponse searchBookingsResponse);

}
