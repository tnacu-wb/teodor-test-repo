package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiExtraItems;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in.CcuiExtraItemsDto;

@Mapper(componentModel = "spring")
public interface CcuiExtraItemsMapper {
  CcuiExtraItemsDto toCcuiExtraItemDto(CcuiExtraItems extraItems);
}
