package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.SearchBookingsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.SearchBookingsRequestDto;

@Mapper(componentModel = "spring")
public interface SearchBookingsRequestMapper {

  SearchBookingsRequest toModel(SearchBookingsRequestDto searchBookingsRequestDto);

}
