package uk.co.whitbread.hotel.register.mapper;

import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.model.RegisterAccountRequest;
import uk.co.whitbread.hotel.register.model.RegisterAccountResponse;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

  @Mapping(source = "guestHistoryNumber", target = "bartGuestHistoryNumber")
  @Mapping(source = "guestHistoryCreation", target = "bartGuestHistoryCreation")
  @Mapping(source = "customer.contactDetail.address.countryCode", target = "contactDetail.address.country")
  @Mapping(source = "customer.contactDetail.address.line1", target = "contactDetail.address.addressLine1")
  @Mapping(source = "customer.contactDetail.address.line2", target = "contactDetail.address.addressLine2")
  @Mapping(source = "customer.contactDetail.address.line3", target = "contactDetail.address.addressLine3")
  @Mapping(source = "customer.contactDetail.address.line4", target = "contactDetail.address.addressLine4")
  @Mapping(source = "customer.contactDetail.address.line5", target = "contactDetail.address.addressLine5")
  CustomerAccountRequest toCdhRequest(Customer customer, String guestHistoryNumber,
      LocalDate guestHistoryCreation);

  @Mapping(source = "email", target = "customerId")
  @Mapping(target = "success", constant = "true")
  @Mapping(target = "sessionId", constant = "deprecated")
  CustomerResponse toCustomerResponse(String email);

  default CustomerAccountRequest toCdhRequest(Customer customer) {
    return toCdhRequest(customer, customer.getGuestHistoryNumber(), null);
  }

  @Mapping(source = "email", target = "contactDetail.email")
  @Mapping(source = "firstName", target = "contactDetail.firstName")
  @Mapping(source = "lastName", target = "contactDetail.lastName")
  @Mapping(target = "contactDetail.address", expression = "java(new uk.co.whitbread.shared.cdh.model.Address())")
  CustomerAccountRequest toCdhRequest(RegisterAccountRequest registerAccountRequest);

  @Mapping(source = "customerAccountId", target = "customerId")
  RegisterAccountResponse toCustomerResponse(CustomerAccountResponse customerAccountResponse);
}
