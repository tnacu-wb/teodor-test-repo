package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.SpecialRequests;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.SpecialRequestsDto;

@Mapper(componentModel = "spring")
public interface SpecialRequestsMapper {

  SpecialRequests toModel(SpecialRequestsDto specialRequestsDto);
}
