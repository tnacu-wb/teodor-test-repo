package uk.co.whitbread.infrastructure.rest.client.promotion.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.promo.generated.models.promotion.PromoKindResponseDto;

@Mapper(componentModel = "spring")
public interface PromoKindResponseMapper {

  PromoKindResponse toModel(PromoKindResponseDto promoKindResponseDto);
}
