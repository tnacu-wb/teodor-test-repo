package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItemsRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BusinessItemsRequestDto;

@Mapper(componentModel = "spring")
public interface BusinessItemsRequestMapper {
  BusinessItemsRequest toModel(BusinessItemsRequestDto dto);
}
