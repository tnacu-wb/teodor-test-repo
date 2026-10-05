package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CancelBasketDto;
import uk.co.whitbread.reservation.domain.model.out.Deposits;

@Mapper(componentModel = "spring")
public interface CancelBasketRequestMapper {

  CancelBasketDto toDto(final Boolean isFailed, final Boolean sendEmail,
      final List<Deposits> deposits);

}
