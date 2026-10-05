package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.AddressResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.AddressResponseDto;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  @Mapping(target = "companyName", source = "companyName", defaultValue = "")
  @Mapping(target = "country", source = "countryCode", defaultValue = "")
  @Mapping(target = "addressLine1", source = "line1", defaultValue = "")
  @Mapping(target = "addressLine2", source = "line2", defaultValue = "")
  @Mapping(target = "addressLine3", source = "line3", defaultValue = "")
  @Mapping(target = "addressLine4", source = "line4", defaultValue = "")
  @Mapping(target = "postalCode", source = "postalCode", defaultValue = "")
  AddressResponseDto toDto(AddressResponse address);

}
