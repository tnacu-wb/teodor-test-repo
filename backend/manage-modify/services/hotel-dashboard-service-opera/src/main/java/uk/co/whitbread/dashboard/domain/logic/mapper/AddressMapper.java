package uk.co.whitbread.dashboard.domain.logic.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoAddress;
import uk.co.whitbread.dashboard.domain.model.out.Address;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AddressMapper {

  @Mapping(target = "addressLine1", source = "addressline1")
  @Mapping(target = "addressLine2", source = "addressline2")
  @Mapping(target = "addressLine3", source = "addressline3")
  @Mapping(target = "addressLine4", source = "addressline4")
  @Mapping(target = "addressLine5", source = "addressline5")
  @Mapping(target = "country", source = "country")
  @Mapping(target = "postCode", source = "postcode")
  Address infoAddressToAddress(InfoAddress address);

}
