package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import java.util.Arrays;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomRateAvailabilityResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomRateType;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomRate;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityByIdsSearchCriteriaV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.CorporateRateDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestDto;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {
    Arrays.class})
public interface AvailabilityByIdsRequestMapper {

  @Mapping(target = "roomStayStartDate", source = "arrivalDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "roomStayEndDate", source = "departureDate", dateFormat = "yyyy-MM-dd")
  @Mapping(target = "roomStayQuantity", source = "numberOfRooms")
  MultiHotelAvailabilityRequestDto toRequestDto(AvailabilityByIdsSearchRequest availabilityRequest);


  @Mapping(source = "rates.corporateRates",
        target = "rates.corporateRates",
        qualifiedByName = "toCorporateRatesDto")
  AvailabilityByIdsSearchCriteriaV2Dto toRequestV2Dto(
        AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria);


  CorporateRateDto toCorporateRateDto(CorporateRate rate);


  @Named("toCorporateRatesDto")
  default CorporateRateDto toCorporateRatesDto(List<CorporateRate> values) {
    if (values == null || values.isEmpty()) {
      return null;
    }
    CorporateRate corporateRate = values.get(0);
    return CorporateRateDto.builder()
        .corporateId(corporateRate.getCorporateId())
        .ratePlanSets(corporateRate.getRatePlanSets())
        .build();
  }

  List<CorporateRateDto> toCorporateRateModel(List<CorporateRate> rates);

  AvailabilityByIdsResultV2 toResultV2Model(
      MultiRoomRateAvailabilityResponseType hotelAvailabilityByIdsRequestV2);

  @Mapping(target = "displaySet", source = "ratePlanSet")
  RoomRate toRoomRateV2Model(
      MultiRoomRateType multiRoomRateType);
}
