package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.exceptions.RateCodePricingException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.RateCodePricingRequestDto;

@Mapper(componentModel = "spring")
public interface RateCodeCriteriaDomainMapper {

  @Mapping(source = "rateCodePricingRqDto", target = "roomInfoCriteriaList", qualifiedByName = "toRoomRateInfo")
  RateCodeCriteria toDomainModel(String hotelId, RateCodePricingRequestDto rateCodePricingRqDto);

  @Named("toRoomRateInfo")
  default List<RateCodeRoomInfoCriteria> toRoomRateInformationModel(
      RateCodePricingRequestDto rateCodePricingRqDto) {
    validateArrayLength(rateCodePricingRqDto);
    List<RateCodeRoomInfoCriteria> rateCodeRoomInfoCriteria = new ArrayList<>();
    for (int i = 0; i < rateCodePricingRqDto.getRoomTypes().size(); i++) {
      rateCodeRoomInfoCriteria.add(RateCodeRoomInfoCriteria
          .builder()
          .roomType(rateCodePricingRqDto.getRoomTypes().get(i))
          .adultsNo(rateCodePricingRqDto.getAdultsNo().get(i))
          .childrenNo(extractChildrenNo(rateCodePricingRqDto, i))
          .build());
    }
    return rateCodeRoomInfoCriteria;
  }

  private void validateArrayLength(RateCodePricingRequestDto rateCodePricingRqDto) {
    if (rateCodePricingRqDto.getRoomTypes().size() != rateCodePricingRqDto.getAdultsNo().size()
        || rateCodePricingRqDto.getChildrenNo().size() > rateCodePricingRqDto.getRoomTypes()
        .size()) {
      throw new RateCodePricingException(ErrorCode.DIGITAL_RATE_CODE_PRICING_EXCEPTION,
          "Length of the adult and room array cannot be different");
    }
  }

  private Integer extractChildrenNo(RateCodePricingRequestDto rateCodePricingRqDto, int i) {
    List<Integer> childrenNo = rateCodePricingRqDto.getChildrenNo();
    if (Objects.isNull(childrenNo) || childrenNo.size() <= i) {
      return 0;
    }
    return Objects.isNull(childrenNo.get(i)) ? 0 : childrenNo.get(i);
  }
}
