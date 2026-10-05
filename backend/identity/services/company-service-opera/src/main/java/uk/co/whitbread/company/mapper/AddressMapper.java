package uk.co.whitbread.company.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.company.model.Address;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  @Mapping(target = "addressLine1", source = "addressLine1")
  @Mapping(target = "addressLine2", source = "addressLine2")
  @Mapping(target = "addressLine3", source = "addressLine3")
  @Mapping(target = "addressLine4", source = "addressLine4")
  @Mapping(target = "addressLine5", source = "addressLine5")
  @Mapping(target = "postCode", source = "postCode")
  Address toAddress(uk.co.whitbread.company.model.Address address);

  @Mapping(target = "country", source = "countryCode")
  Address toAddress(BusinessAddress address);

  @InheritInverseConfiguration
  BusinessAddress toBusinessAddress(Address address);
}
