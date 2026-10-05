package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.checkin.out.Address;
import uk.co.whitbread.ohip.domain.model.profile.in.Country;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BookerAddressDto;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookerAddressMapper {
  default Address toAddressModel(BookerAddressDto bookerAddress) {
    if (bookerAddress == null) {
      return null;
    }
    List<String> addressLines = new ArrayList<>();
    addressLines.add(bookerAddress.getAddressLine1());
    addressLines.add(bookerAddress.getAddressLine2());
    addressLines.add(bookerAddress.getAddressLine3());
    addressLines.add(bookerAddress.getAddressLine4());
    return Address.builder()
        .addressLine(addressLines)
        .cityName(bookerAddress.getCityName())
        .postalCode(bookerAddress.getPostalCode())
        .country(new Country(bookerAddress.getCountryCode()))
        .build();
  }
}
