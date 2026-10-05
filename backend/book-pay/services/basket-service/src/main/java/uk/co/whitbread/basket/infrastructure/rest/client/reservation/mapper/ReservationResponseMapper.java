package uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationResponseMapper {

  ReservationByBasketRefResponse toModel(ReservationByBasketRefResponseDto responseDto);

}
