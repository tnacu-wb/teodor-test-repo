package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateDiscountRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateDiscountRequestMapper {

  UpdateDiscountRequest toModel(UpdateDiscountRequestDto updateDiscountRequestDto);
}
