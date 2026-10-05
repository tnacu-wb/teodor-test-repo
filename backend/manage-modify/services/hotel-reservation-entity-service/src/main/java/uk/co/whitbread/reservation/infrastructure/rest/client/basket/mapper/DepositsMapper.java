package uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.DepositsDto;
import uk.co.whitbread.reservation.domain.model.out.Deposits;

@Mapper(componentModel = "spring")
public interface DepositsMapper {

  DepositsDto toDto(Deposits deposits);

}
