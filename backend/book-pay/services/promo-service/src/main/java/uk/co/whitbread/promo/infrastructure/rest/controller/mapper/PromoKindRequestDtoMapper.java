package uk.co.whitbread.promo.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoKindRequest;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.PromoKindRequestDto;

@Mapper(componentModel = "spring")
public interface PromoKindRequestDtoMapper {
  PromoKindRequest toModel(PromoKindRequestDto requestDto);
}
