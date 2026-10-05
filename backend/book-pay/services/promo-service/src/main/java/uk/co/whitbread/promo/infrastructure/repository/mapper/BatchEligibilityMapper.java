package uk.co.whitbread.promo.infrastructure.repository.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.out.BatchEligibilitySummary;
import uk.co.whitbread.promo.infrastructure.repository.model.BatchEligibilityEntity;

@Mapper(componentModel = "spring")
public interface BatchEligibilityMapper {

  default List<BatchEligibilitySummary> toSummaryModel(
          List<BatchEligibilityEntity> entities) {

    return entities.stream()
            .collect(Collectors.groupingBy(
                    e -> e.getRegion().name() + "|" + e.getChannel().name()))
            .values()
            .stream()
            .map(group -> BatchEligibilitySummary.builder()
                    .region(group.get(0).getRegion())
                    .channel(group.get(0).getChannel())
                    .platforms(group.stream()
                            .map(BatchEligibilityEntity::getPlatform)
                            .toList())
                    .build())
            .toList();
  }
}
