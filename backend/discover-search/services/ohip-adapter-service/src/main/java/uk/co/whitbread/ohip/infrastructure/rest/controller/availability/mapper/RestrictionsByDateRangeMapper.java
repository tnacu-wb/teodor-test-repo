package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.RestrictionsByDateRangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.RestrictionsByDateRangeDto;

@Mapper(componentModel = "spring")
public interface RestrictionsByDateRangeMapper {
  RestrictionsByDateRangeSearchCriteria toDomainModel(String hotelId,
      RestrictionsByDateRangeRequestDto restrictionsByDateRangeDto);

  @Mapping(target = "restrictionSets",
        source = "hotelInventory.restrictionsByDateRange.restrictionsByDateRange.restrictionSets")
  @Mapping(target = "hotelId",
        source = "hotelInventory.restrictionsByDateRange.restrictionsByDateRange.hotelId")
  @Mapping(target = "hasMore",
        source = "hotelInventory.restrictionsByDateRange.restrictionsByDateRange.hasMore")
  RestrictionsByDateRangeDto toDto(RestrictionsByDateRangeResult hotelInventory);
}
