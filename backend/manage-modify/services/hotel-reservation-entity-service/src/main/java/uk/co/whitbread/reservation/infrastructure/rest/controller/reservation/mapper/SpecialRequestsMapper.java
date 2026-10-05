package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.SpecialRequestsDto;

@Mapper(componentModel = "spring")
public interface SpecialRequestsMapper {

  SpecialRequests toModel(SpecialRequestsDto specialRequestsDto);
}
