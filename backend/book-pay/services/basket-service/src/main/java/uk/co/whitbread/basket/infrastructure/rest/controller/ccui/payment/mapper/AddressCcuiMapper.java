package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.AddressCcui;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in.AddressCcuiDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressCcuiMapper {

  @Mapping(target = "countryCode", source = "country")
  @Mapping(target = "line1", source = "addressLine1")
  @Mapping(target = "line2", source = "addressLine2")
  @Mapping(target = "line3", source = "addressLine3")
  @Mapping(target = "line4", source = "addressLine4")
  @Mapping(target = "postalCode", source = "postalCode")
  AddressCcui toModel(AddressCcuiDto address);
}
