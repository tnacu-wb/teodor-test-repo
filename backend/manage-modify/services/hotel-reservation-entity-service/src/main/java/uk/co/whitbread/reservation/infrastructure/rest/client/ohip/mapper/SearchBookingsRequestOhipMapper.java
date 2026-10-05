package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.reservation.domain.model.in.SearchBookingsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model.SearchBookingsRequestDto;

@Mapper(componentModel = "spring", uses = {ReservationOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SearchBookingsRequestOhipMapper {

  SearchBookingsRequestDto toDto(SearchBookingsRequest searchBookingsRequest);

}
