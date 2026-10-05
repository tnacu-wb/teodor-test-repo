package uk.co.whitbread.hotel.register.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.register.model.Address;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.InnBCompanyAddress;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoRequest;
import uk.co.whitbread.shared.cdh.model.BusinessAddress;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EmployeeMapper {

  @Mapping(source = "contactDetail.title", target = "title")
  @Mapping(source = "contactDetail.firstName", target = "firstName")
  @Mapping(source = "contactDetail.lastName", target = "lastName")
  @Mapping(source = "contactDetail.email", target = "emailAddress")
  @Mapping(source = "contactDetail.telephone", target = "phoneNumber")
  @Mapping(source = "contactDetail.mobile", target = "mobileNumber")
  @Mapping(source = "contactDetail.address", target = "address")
  @Mapping(source = "contactDetail.carRegistration", target = "carRegistration")
  @Mapping(target = "bookingPreference.electronicInvoiceRequired",
      source = "paymentPreference.electronicInvoiceRequired")
  @Mapping(target = "bookingPreference.premierBreakfast",
      source = "bookingPreference.foodPreference", qualifiedByName = "toPremierBreakfast")
  @Mapping(target = "bookingPreference.continentalBreakfast",
      source = "bookingPreference.foodPreference", qualifiedByName = "toContinentalBreakfast")
  @Mapping(target = "bookingPreference.mealDeal",
      source = "bookingPreference.foodPreference", qualifiedByName = "toMealDeal")
  EmployeeAccountRequest toEmployeeAccountRequest(Customer customer);

  EmployeeAccountRequest toEmployeeAccountRequest(GetEmployeeResponse response);

  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  @Mapping(target = "phoneNumber", source = "phoneNumber")
  @Mapping(target = "bookingPreference.electronicInvoiceRequired", constant = "true")
  void toEmployeeAccountRequest(InnBRegistrationStepTwoRequest request, @MappingTarget EmployeeAccountRequest employeeAccountRequest);

  @Mapping(source = "address", target = "address")
  @Mapping(source = "email", target = "emailAddress")
  EmployeeAccountRequest toEmployeeAccountRequest(InnBRegistrationStepOneRequest request);

  @Mapping(source = "line1", target = "addressLine1")
  @Mapping(source = "line2", target = "addressLine2")
  @Mapping(source = "line3", target = "addressLine3")
  @Mapping(source = "line4", target = "addressLine4")
  @Mapping(source = "line5", target = "addressLine5")
  BusinessAddress toBusinessAddress(Address address);

  @Mapping(source = "line1", target = "addressLine1")
  @Mapping(source = "line2", target = "addressLine2")
  @Mapping(source = "line3", target = "addressLine3")
  @Mapping(source = "line4", target = "addressLine4")
  @Mapping(source = "line5", target = "addressLine5")
  BusinessAddress toBusinessAddress(InnBCompanyAddress address);

  @Named("toPremierBreakfast")
  default boolean toPremierBreakfast(long foodPreference){
    return foodPreference == 11;
  }

  @Named("toContinentalBreakfast")
  default boolean toContinentalBreakfast(long foodPreference){
    return foodPreference == 12;
  }

  @Named("toMealDeal")
  default boolean toMealDeal(long foodPreference){
    return foodPreference == 17;
  }
}
