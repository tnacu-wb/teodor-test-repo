package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BusinessItemsRequestDto;
import uk.co.whitbread.reservation.domain.model.in.BusinessItemsRequest;

@Mapper(componentModel = "spring")
public interface BusinessItemsRequestOhipMapper {

  BusinessItemsRequestDto toDto(BusinessItemsRequest model);
}
