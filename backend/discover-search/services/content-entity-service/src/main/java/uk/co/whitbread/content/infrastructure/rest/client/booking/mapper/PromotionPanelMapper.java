package uk.co.whitbread.content.infrastructure.rest.client.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.booking.out.PromotionPanel;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.PromotionPanelDto;

@Mapper(componentModel = "spring")
public interface PromotionPanelMapper {

  @Mapping(target = "name", source = "title")
  PromotionPanel toDomainModel(PromotionPanelDto donationDto);

}
