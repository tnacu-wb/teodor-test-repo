package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateAllowancesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateBasketItemSupplementDto;
import uk.co.whitbread.reservation.domain.model.basket.allowances.UpdateAllowancesRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;

@Mapper(componentModel = "spring")
public interface BasketUpdateRequestMapper {
  UpdateAllowancesRequestDto toUpdateAllowancesRequestDto(UpdateAllowancesRequest updateAllowancesRequest);

  UpdateBasketItemSupplementDto toUpdateBasketItemOccupancyRequestDto(
      BasketItemResponse basketReservationReference);
}
