package uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlansSummary;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlansResponse;

@Mapper(componentModel = "spring")
public interface RatePlansMapper {
  @Mapping(target = "ratePlans", source = "ratePlanShortInfoList.ratePlanShortInfo")
  RatePlansResponse toDomainModel(RatePlansSummary ratePlansSummary);

}
