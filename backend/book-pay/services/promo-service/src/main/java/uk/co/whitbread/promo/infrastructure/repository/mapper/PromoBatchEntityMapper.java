package uk.co.whitbread.promo.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchResponse;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;

@Mapper(componentModel = "spring")
public interface PromoBatchEntityMapper {

  PromoBatchResponse toModel(PromoBatchEntity amendmentRuleEntity);
}
