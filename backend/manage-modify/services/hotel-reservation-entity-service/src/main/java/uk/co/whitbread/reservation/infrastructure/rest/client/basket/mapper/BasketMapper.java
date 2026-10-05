package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ErroredBookingDto;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;

@Mapper(componentModel = "spring")
public interface BasketMapper {

  BasketResponse toModel(BasketDto basket);

  ErroredBookingDto toDto(Boolean isErroredBooking, BasketError basketError);

}
