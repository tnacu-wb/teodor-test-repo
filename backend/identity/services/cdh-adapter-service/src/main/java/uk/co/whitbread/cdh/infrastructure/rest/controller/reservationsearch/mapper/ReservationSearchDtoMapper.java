package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out.CdhReservationSearchDto;

@Mapper(componentModel = "spring")
public interface ReservationSearchDtoMapper {

  CdhReservationSearchDto toDto(ReservationSearch reservationSearch);
}
