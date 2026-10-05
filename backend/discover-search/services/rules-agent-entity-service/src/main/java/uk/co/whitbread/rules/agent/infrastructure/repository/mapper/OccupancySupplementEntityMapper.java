package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplement;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.OccupancySupplementEntity;

@Mapper(componentModel = "spring")
public interface OccupancySupplementEntityMapper {

  OccupancySupplement toModel(OccupancySupplementEntity entity);
}
