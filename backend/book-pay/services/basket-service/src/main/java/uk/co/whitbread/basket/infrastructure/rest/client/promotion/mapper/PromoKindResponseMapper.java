package uk.co.whitbread.basket.infrastructure.rest.client.promotion.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.basket.generated.models.promotion.PromoKindResponseDto;

@Mapper(componentModel = "spring")
public interface PromoKindResponseMapper {

  PromoKindResponse toModel(PromoKindResponseDto promoKindResponseDto);
}
