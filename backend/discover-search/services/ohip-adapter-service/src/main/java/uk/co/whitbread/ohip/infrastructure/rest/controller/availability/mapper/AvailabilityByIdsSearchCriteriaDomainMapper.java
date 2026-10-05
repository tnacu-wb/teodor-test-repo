package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.ohip.domain.model.availability.in.RateV2;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityByIdsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityByIdsRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityByIdsRequestV3Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.CorporateRateDto;

@Mapper(componentModel = "spring")
public interface AvailabilityByIdsSearchCriteriaDomainMapper {

  AvailabilityByIdsSearchCriteria toDomainModel(
      AvailabilityByIdsRequestDto hotelAvailabilityRequest);

  AvailabilityByIdsSearchCriteriaV2 toV2DomainModel(
      AvailabilityByIdsRequestV2Dto hotelAvailabilityRequest);

  AvailabilityByIdsSearchCriteriaV2 toV3DomainModel(
      AvailabilityByIdsRequestV3Dto hotelAvailabilityRequest);


  private CorporateRate toCorporateRate(CorporateRateDto corporateRateDto) {
    return CorporateRate.builder()
        .corporateId(corporateRateDto.getCorporateId())
        .ratePlanSets(corporateRateDto.getRatePlanSets())
        .build();
  }

  default RateV2 toRateV2Dto(List<CorporateRateDto> corporateRateDtoList) {
    if (corporateRateDtoList == null || corporateRateDtoList.isEmpty()) {
      return null;
    }
    return RateV2.builder()
        .corporateRates(corporateRateDtoList.stream()
            .map(this::toCorporateRate)
            .toList())
        .build();
  }

  default List<CorporateRate> toCorporateRateDto(CorporateRateDto value) {
    if (value == null) {
      return Collections.emptyList();
    }
    CorporateRate corporateRate = CorporateRate.builder()
        .corporateId(value.getCorporateId())
        .ratePlanSets(value.getRatePlanSets())
        .build();
    return Collections.singletonList(corporateRate);
  }


}
