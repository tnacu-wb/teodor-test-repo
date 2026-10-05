package uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.in.Address;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.AddressDto;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapperRequest {

  @Mapping(target = "countryCode", source = "country")
  @Mapping(target = "line1", source = "addressLine1")
  @Mapping(target = "line2", source = "addressLine2")
  @Mapping(target = "line3", source = "addressLine3")
  @Mapping(target = "line4", source = "addressLine4")
  @Mapping(target = "postalCode", source = "postalCode")
  @Mapping(target = "companyName", source = "companyName")
  @Mapping(target = "addressType", source = "addressType")
  Address toModel(AddressDto address);

}