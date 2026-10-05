package uk.co.whitbread.promo.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoBatchRequest;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.PromoBatchRequestDto;

@Mapper(componentModel = "spring")
public interface PromoBatchRequestDtoMapper {

  PromoBatchRequest toModel(PromoBatchRequestDto promoBatchRequestDto);
}
