package uk.co.whitbread.basket.domain.logic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.basket.domain.model.payments.in.Address;
import uk.co.whitbread.basket.domain.model.payments.out.CompanyAddress;

@Mapper(componentModel = "spring")
public interface CompanyAddressMapper {

  @Mapping(target = "line1", source = "addressLine1", defaultValue = "")
  @Mapping(target = "line2", source = "addressLine2", defaultValue = "")
  @Mapping(target = "line3", source = "addressLine3", defaultValue = "")
  @Mapping(target = "line4", source = "addressLine4", defaultValue = "")
  @Mapping(target = "companyName", source = "companyName", defaultValue = "")
  @Mapping(target = "countryCode", source = "countryCode", defaultValue = "")
  @Mapping(target = "postalCode", source = "postCode", defaultValue = "")
  Address toAddress(CompanyAddress address);

}
