package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

  @Mapping(source = "addressline1", target = "addressLine1")
  @Mapping(source = "addressline2", target = "addressLine2")
  @Mapping(source = "addressline3", target = "addressLine3")
  @Mapping(source = "postcode", target = "postalCode")
  uk.co.whitbread.content.domain.model.hotel.out.Address toDomainModel(Address address);

}
