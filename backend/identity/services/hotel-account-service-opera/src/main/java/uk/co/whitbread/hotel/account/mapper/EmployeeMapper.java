package uk.co.whitbread.hotel.account.mapper;

import java.util.Objects;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.account.model.AccessLevel;
import uk.co.whitbread.hotel.account.model.Address;
import uk.co.whitbread.hotel.account.model.BillingAddress;
import uk.co.whitbread.hotel.account.model.BookingPreference;
import uk.co.whitbread.hotel.account.model.BookingType;
import uk.co.whitbread.hotel.account.model.Business;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.model.CustomerResponse;
import uk.co.whitbread.hotel.account.utils.EnumConverter;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.BusinessBookingPreference;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Mapper(componentModel = "spring", uses = {AddressTypeMapper.class, CardNumberMapper.class})
public abstract class EmployeeMapper {

  public abstract EmployeeAccountRequest toEmployeeAccountRequest(GetEmployeeResponse response);

  @Mapping(target = "title", source = "contactDetail.title")
  @Mapping(target = "firstName", source = "contactDetail.firstName")
  @Mapping(target = "lastName", source = "contactDetail.lastName")
  @Mapping(target = "emailAddress", source = "contactDetail.email")
  @Mapping(target = "phoneNumber", source = "contactDetail.telephone")
  @Mapping(target = "mobileNumber", source = "contactDetail.mobile")
  @Mapping(target = "carRegistration", source = "contactDetail.carRegistration")
  @Mapping(target = "bartGuestHistoryNumber", source = "guestHistoryNumber")
  @Mapping(target = "address", source = "contactDetail.address")
  @Mapping(target = "paymentPreference.paymentCard.cardNumber",
      source = "paymentPreference.paymentCard.cardNumber", qualifiedByName = "maskCardNumber")
  @Mapping(target = "paymentPreference.paymentCard.token",
      source = "paymentPreference.paymentCard.cardToken")
  @Mapping(target = "bookingPreference.electronicInvoiceRequired",
      source = "paymentPreference.electronicInvoiceRequired")
  @Mapping(target = "bookingPreference.premierBreakfast",
      source = "bookingPreference.foodPreference", qualifiedByName = "toPremierBreakfast")
  @Mapping(target = "bookingPreference.continentalBreakfast",
      source = "bookingPreference.foodPreference", qualifiedByName = "toContinentalBreakfast")
  @Mapping(target = "bookingPreference.mealDeal",
      source = "bookingPreference.foodPreference", qualifiedByName = "toMealDeal")
  public abstract EmployeeAccountRequest toEmployeeAccountRequest(CustomerRequest partialUpdateRequest,
      @MappingTarget EmployeeAccountRequest updateRequest);

  @Mapping(target = "addressLine1", source = "line1")
  @Mapping(target = "addressLine2", source = "line2")
  @Mapping(target = "addressLine3", source = "line3")
  @Mapping(target = "addressLine4", source = "line4")
  @Mapping(target = "addressLine5", source = "line5")
  abstract BusinessAddress toBusinessAddress(Address address);

  @Mapping(target = "addressLine1", source = "line1")
  @Mapping(target = "addressLine2", source = "line2")
  @Mapping(target = "addressLine3", source = "line3")
  @Mapping(target = "addressLine4", source = "line4")
  @Mapping(target = "addressLine5", source = "line5")
  abstract BusinessAddress toBusinessAddress(BillingAddress address);

  @Mapping(target = "line1", source = "addressLine1")
  @Mapping(target = "line2", source = "addressLine2")
  @Mapping(target = "line3", source = "addressLine3")
  @Mapping(target = "line4", source = "addressLine4")
  @Mapping(target = "line5", source = "addressLine5")
  abstract BillingAddress toBusinessAddress(BusinessAddress businessAddress);

  @InheritInverseConfiguration
  abstract Address toAddress(BusinessAddress address);

  @Mapping(target = "companyId", expression = "java(injectCompanyId(response))")
  @Mapping(target = "contactDetail.title", source = "title")
  @Mapping(target = "contactDetail.firstName", source = "firstName")
  @Mapping(target = "contactDetail.lastName", source = "lastName")
  @Mapping(target = "contactDetail.email", source = "emailAddress")
  @Mapping(target = "contactDetail.telephone", source = "phoneNumber")
  @Mapping(target = "contactDetail.mobile", source = "mobileNumber")
  @Mapping(target = "contactDetail.carRegistration", source = "carRegistration")
  @Mapping(target = "contactDetail.address", source = "address")
  @Mapping(target = "guestHistoryNumber", source = "bartGuestHistoryNumber")
  @Mapping(target = "guestHistoryCreation", source = "bartGuestHistoryCreation")
  @Mapping(target = "paymentPreference.electronicInvoiceRequired",
      source = "bookingPreference.electronicInvoiceRequired")
  @Mapping(target = "paymentPreference.paymentCard.cardToken",
      source = "paymentPreference.paymentCard.token")
  @Mapping(target = "business", expression = "java(toBusiness(response))")
  public abstract Customer toCustomer(GetEmployeeResponse response);

  @Mapping(target = "employeeId", expression = "java(injectEmployeeId(response))")
  @Mapping(target = "accessLevel", expression = "java(toAccessLevel(response.getAccessLevel()))")
  @Mapping(target = "centralCard", expression = "java(org.apache.commons.lang3.StringUtils.isNotEmpty(response.getCentralCardIdString()) ? "
      + "response.getCentralCardIdString() : response.getCentralCardId())")
  @Mapping(target = "awaitingApproval",
      expression = "java(response.isAwaitingApproval() ? 1L :0L)")
  @Mapping(target = "dismissMPILink", source = "business.dismissMPILink")
  @Mapping(target = "myPILink", source = "business.myPILink")
  @Mapping(target = "miSetupRequired", source = "business.miSetupRequired")
  @Mapping(target = "tethered", source = "business.tethered")
  @Mapping(target = "customerReferenceAnswer", source = "employeeAnswers.customerReferenceAnswer")
  @Mapping(target = "purchaseOrderAnswer", source = "employeeAnswers.purchaseOrderAnswer")
  abstract Business toBusiness(GetEmployeeResponse response);

  @Mapping(target = "customerId", source = "employeeAccountId")
  @Mapping(target = "success", constant = "true")
  public abstract CustomerResponse toCustomerResponse(EmployeeAccountResponse response);

  @Mapping(target = "foodPreference", expression = "java(toFoodPreference(bookingPreference))")
  @Mapping(target = "reason", expression = "java(toBookingType(bookingPreference.getReason()))")
  abstract BookingPreference toBookingPreference(BusinessBookingPreference bookingPreference);

  @Named("toPremierBreakfast")
  boolean toPremierBreakfast(long foodPreference){
    return foodPreference == 11;
  }

  @Named("toContinentalBreakfast")
  boolean toContinentalBreakfast(long foodPreference){
    return foodPreference == 12;
  }

  @Named("toMealDeal")
  boolean toMealDeal(long foodPreference){
    return foodPreference == 17;
  }

  long toFoodPreference(BusinessBookingPreference bookingPreference) {
    if (Objects.isNull(bookingPreference)) {
      return 0;
    }
    if (Boolean.TRUE.equals(bookingPreference.getPremierBreakfast())) {
      return 11;
    }
    if (Boolean.TRUE.equals(bookingPreference.getContinentalBreakfast())) {
      return 12;
    }
    if (Boolean.TRUE.equals(bookingPreference.getMealDeal())) {
      return 17;
    }
    return 0;
  }

  BookingType toBookingType(String bookingReason) {
    return EnumConverter.getEnum(BookingType.class, bookingReason);
  }

  AccessLevel toAccessLevel(String accessLevel) {
    return EnumConverter.getEnum(AccessLevel.class, accessLevel);
  }

  String injectCompanyId(GetEmployeeResponse response) {
      return response.getCompanyAccountId();
  }

  String injectEmployeeId(GetEmployeeResponse response) {
      return response.getEmployeeAccountId();
  }
}
