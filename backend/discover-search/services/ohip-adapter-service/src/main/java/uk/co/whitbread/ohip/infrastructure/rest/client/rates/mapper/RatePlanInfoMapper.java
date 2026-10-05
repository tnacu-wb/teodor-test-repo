package uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.RatePlanInfo;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;

@Mapper(componentModel = "spring")
public interface RatePlanInfoMapper {

  @Mapping(target = "ratePlanInfo", source = "ratePlanInfo.ratePlans")
  RatePlanInfoResponse toDomainModel(RatePlanInfo ratePlanInfo);
}
