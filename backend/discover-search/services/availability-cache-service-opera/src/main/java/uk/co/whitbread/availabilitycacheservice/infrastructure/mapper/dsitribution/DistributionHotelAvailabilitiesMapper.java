package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.dsitribution;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRatePlan;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionRatePlanDto;

@Mapper(componentModel = "spring")
public interface DistributionHotelAvailabilitiesMapper {

  DistributionHotelAvailabilitiesMapper INSTANCE =
      Mappers.getMapper(DistributionHotelAvailabilitiesMapper.class);

  @Mapping(source = "distributionRatePlan.code", target = "ratePlanCode")
  DistributionRatePlanDto toDistributionRatePlanDto(DistributionRatePlan distributionRatePlan);

  DistributionHotelDto toDistributionHotelDto(DistributionHotel distributionHotel);

  List<DistributionHotelDto> toDistributionHotelDtoList(List<DistributionHotel> distributionHotelList);

}
