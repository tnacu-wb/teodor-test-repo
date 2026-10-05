package uk.co.whitbread.promo.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummary;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;
import uk.co.whitbread.promo.infrastructure.repository.projection.PromoBatchSummaryProjection;

@Mapper(componentModel = "spring")
public interface PromoBatchSummaryMapper {

  PromoBatchSummary toModel(PromoBatchEntity promoBatchEntity);

  PromoBatchSummary toSummaryModel(PromoBatchSummaryProjection promoBatchSummaryProjection);
}