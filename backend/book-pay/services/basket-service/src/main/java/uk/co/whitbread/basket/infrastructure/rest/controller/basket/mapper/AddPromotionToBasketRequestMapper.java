package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.PromotionsInformationRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PromotionsInformationRequestDto;

@Mapper(componentModel = "spring")
public interface AddPromotionToBasketRequestMapper {

  PromotionsInformationRequest toDomainModel(
      @Valid PromotionsInformationRequestDto updateAllowancesRequestDto);
}
