package uk.co.whitbread.hotel.register.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.register.model.Address;
import uk.co.whitbread.hotel.register.model.AppsContactDetail;
import uk.co.whitbread.hotel.register.model.AppsCustomer;
import uk.co.whitbread.hotel.register.model.ContactDetail;
import uk.co.whitbread.hotel.register.model.Customer;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AccountMapper {

  @Mapping(target = "additionalGuests", expression = "java(java.util.Collections.emptyList())")
  @Mapping(target = "contactDetail", expression = "java(mapDetails(request.getContactDetail()))")
  Customer toAccountRequest(AppsCustomer request);

  default ContactDetail mapDetails(AppsContactDetail request) {
    if (request == null) {
      return null;
    }
    ContactDetail details = new ContactDetail();
    details.setFirstName(request.getFirstName());
    details.setLastName(request.getLastName());
    details.setEmail(request.getEmail());
    details.setCarRegistration("");
    details.setAddress(Address.builder().companyName("").postCode("").build());
    return details;
  }
}