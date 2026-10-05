package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.UpdateDiscountRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateDiscountRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateDiscountRequestMapper {

  UpdateDiscountRequest toModel(UpdateDiscountRequestDto updateDiscountRequestDto);
}
