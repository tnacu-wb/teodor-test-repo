package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.BusinessItemsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BusinessItemsRequestDto;

@Mapper(componentModel = "spring")
public interface BusinessItemsRequestMapper {

  BusinessItemsRequest toModel(BusinessItemsRequestDto dto);
}
