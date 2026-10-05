package uk.co.whitbread.content.infrastructure.rest.client.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.booking.out.Item;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.ItemDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

  @Mapping(source = "bartCode", target = "bartId")
  Item toDto(ItemDto rateInformationRequest);
}
