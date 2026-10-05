package uk.co.whitbread.hotel.account.mapper;


import static uk.co.whitbread.hotel.account.utils.EnumConverter.getEnum;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.account.model.AddressType;

@Mapper(componentModel = "spring")
public interface AddressTypeMapper {

  default AddressType toAddressType(String addressType) {
    return getEnum(AddressType.class, addressType);
  }
}
