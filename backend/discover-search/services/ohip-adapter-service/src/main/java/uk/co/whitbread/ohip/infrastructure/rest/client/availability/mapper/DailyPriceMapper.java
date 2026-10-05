package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityDailyPrice;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.DetailDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DailyPriceMapper {

  @Mapping(target = "date", source = "summaryDate")
  @Mapping(target = "netPrice", source = "net")
  @Mapping(target = "grossPrice", source = "gross")
  AvailabilityDailyPrice toDomainModel(DetailDto detailDto);

}
