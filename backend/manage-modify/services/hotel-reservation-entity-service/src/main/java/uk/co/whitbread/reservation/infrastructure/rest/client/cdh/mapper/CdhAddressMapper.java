package uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.AddressDto;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestAddress;

@Mapper(componentModel = "spring")
public interface CdhAddressMapper {

  @Mapping(source = "type", target = "addressType")
  @Mapping(source = "postCode", target = "postalCode")
  @Mapping(source = "addressLine4", target = "cityName")
  @Mapping(source = "addressLine5", target = "addressLine4")
  StayingGuestAddress toStayingGuestAddressModel(AddressDto addressDto);
}
