package uk.co.whitbread.company.mapper;

import static uk.co.whitbread.company.utils.EnumConverter.getEnum;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.company.client.model.Customer;
import uk.co.whitbread.company.model.Employee;
import uk.co.whitbread.company.model.EmployeeStatus;
import uk.co.whitbread.company.model.MainContact;
import uk.co.whitbread.company.model.UserDefinedAnswer;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EmployeeMapper {

  @Mapping(target = "companyId", source = "companyAccountId")
  @Mapping(target = "business.accessLevel", qualifiedByName = "toClientAccessLevel", source = "accessLevel")
  @Mapping(target = "business.employeeId", source = "employeeAccountId")
  Customer toCustomer(GetEmployeeResponse response);

  @Mapping(target = "id", source = "employeeAccountId")
  @Mapping(target = "ghNumber", source = "bartGuestHistoryNumber")
  @Mapping(target = "address.country", source = "address.countryCode")
  @Mapping(target = "employeeStatus", qualifiedByName = "toEmployeeStatus", source = "employeeStatus")
  @Mapping(target = "accessLevel", qualifiedByName = "toAccessLevel", source = "accessLevel")
  Employee toEmployee(GetEmployeeResponse response);

  @Mapping(target = "title", source = "mainContact.title")
  @Mapping(target = "firstName", source = "mainContact.firstName")
  @Mapping(target = "lastName", source = "mainContact.lastName")
  @Mapping(target = "emailAddress", source = "mainContact.emailAddress")
  @Mapping(target = "mobileNumber", source = "mainContact.mobileNumber")
  @Mapping(target = "phoneNumber", source = "mainContact.phoneNumber")
  @Mapping(target = "position", source = "mainContact.position")
  @Mapping(target = "textConfirmation", source = "mainContact.textConfirmation")
  EmployeeAccountRequest toEmployeeAccountRequest(GetEmployeeResponse response, MainContact mainContact);

  @Mapping(target = "miID", source = "questionId")
  @Mapping(target = "miAnswer", source = "answer")
  UserDefinedAnswer toUserDefinedAnswer(uk.co.whitbread.shared.cdh.model.UserDefinedAnswer userDefinedAnswer);

  @Named("toEmployeeStatus")
  default EmployeeStatus toEmployeeStatus(String status) {
    return getEnum(EmployeeStatus.class, status);
  }

  @Named("toClientAccessLevel")
  default uk.co.whitbread.company.client.model.AccessLevel toClientAccessLevel(String accessLevel) {
    return getEnum(uk.co.whitbread.company.client.model.AccessLevel.class, accessLevel);
  }

  @Named("toAccessLevel")
  default uk.co.whitbread.company.model.AccessLevel toAccessLevel(String accessLevel) {
    return getEnum(uk.co.whitbread.company.model.AccessLevel.class, accessLevel);
  }
}
