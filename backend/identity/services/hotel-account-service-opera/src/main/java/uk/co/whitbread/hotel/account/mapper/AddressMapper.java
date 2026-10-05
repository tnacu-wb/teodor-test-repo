package uk.co.whitbread.hotel.account.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.account.model.BillingAddress;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AddressMapper {

    @Mapping(target = "addressLine1", source = "line1")
    @Mapping(target = "addressLine2", source = "line2")
    @Mapping(target = "addressLine3", source = "line3")
    @Mapping(target = "addressLine4", source = "line4")
    @Mapping(target = "addressLine5", source = "line5")
    @Mapping(target = "country", source = "countryCode")
    uk.co.whitbread.shared.cdh.model.Address billingAddressToCdhAddressModel(BillingAddress billingAddress);
}
