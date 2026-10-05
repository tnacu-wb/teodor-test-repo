package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateDiscountRequestDto;
import uk.co.whitbread.reservation.domain.model.in.UpdateDiscountRequest;


@Mapper(componentModel = "spring")
public interface UpdateDiscountRequestOhipMapper {

  UpdateDiscountRequestDto toDto(UpdateDiscountRequest updateDiscountRequest);

}
