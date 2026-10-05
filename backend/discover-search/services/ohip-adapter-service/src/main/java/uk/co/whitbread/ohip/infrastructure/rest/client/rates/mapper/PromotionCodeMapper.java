package uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PropertyPromotionCodeType;
import uk.co.whitbread.ohip.domain.model.rates.out.PromotionCodeResponse;

@Mapper(componentModel = "spring")
public interface PromotionCodeMapper {

  @Mapping(target = "promotionName", source = "promotionCodeDetails.promotionName.defaultText")
  @Mapping(target = "bookingStartDate", source = "promotionCodeDetails.bookingDate.startDate")
  @Mapping(target = "bookingEndDate", source = "promotionCodeDetails.bookingDate.endDate")
  @Mapping(target = "stayStartDate", source = "promotionCodeDetails.stayDate.startDate")
  @Mapping(target = "stayEndDate", source = "promotionCodeDetails.stayDate.endDate")
  @Mapping(source = "promotionCode", target = "promotionCode")
  PromotionCodeResponse toDomainModel(PropertyPromotionCodeType codeList);

}




