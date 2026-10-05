package uk.co.whitbread.reservation.infrastructure.rest.client.promotion.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.service.generated.models.promotion.PromoKindResponseDto;
import uk.co.whitbread.reservation.domain.model.promotion.out.PromoKindResponse;

@Mapper(componentModel = "spring")
public interface PromoKindResponseMapper {

  PromoKindResponse toModel(PromoKindResponseDto promoKindResponseDto);
}
