package uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.account.service.generated.models.Address;
import uk.co.whitbread.hotel.account.service.generated.models.Address.TypeEnum;
import uk.co.whitbread.reservation.domain.model.in.BookerAddress;

@Mapper(componentModel = "spring")
public interface CustomerAddressMapper {

  @Mapping(target = "type", source = "addressType", qualifiedByName = "mapAddressType")
  @Mapping(target = "postCode", source = "postalCode")
  @Mapping(target = "line1", source = "addressLine1")
  @Mapping(target = "line2", source = "addressLine2")
  @Mapping(target = "line3", source = "addressLine3")
  @Mapping(target = "line4", source = "addressLine4")
  Address toDto(BookerAddress bookerAddress);

  @Named("mapAddressType")
  default TypeEnum toAddressTypeDto(String addressType) {
    return TypeEnum.valueOf(addressType.toUpperCase());
  }
}
