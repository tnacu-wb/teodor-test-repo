package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomPriceBreakdown;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.SummaryDto;

@Mapper(componentModel = "spring", uses = {DailyPriceMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PriceBreakdownMapper {

  @Mapping(target = "dailyPrices", source = "details")
  @Mapping(target = "totalNetAmount", source = "net")
  @Mapping(target = "totalGrossAmount", source = "gross")
  @Mapping(target = "totalTaxAmount",
      expression = "java(priceBreakdown.getNet().subtract(priceBreakdown.getGross()))")
  AvailabilityRoomPriceBreakdown toDomainModel(SummaryDto priceBreakdown);

}
