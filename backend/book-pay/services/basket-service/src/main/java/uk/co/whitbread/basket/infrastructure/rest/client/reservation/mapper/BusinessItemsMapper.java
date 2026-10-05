package uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.generated.models.reservation.BusinessItemsDto;

@Mapper(componentModel = "spring")
public interface BusinessItemsMapper {

  BusinessItemsDto toDto(BusinessItems businessItems);
}
