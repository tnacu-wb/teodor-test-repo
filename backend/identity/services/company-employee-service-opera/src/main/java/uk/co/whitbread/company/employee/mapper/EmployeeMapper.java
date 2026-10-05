package uk.co.whitbread.company.employee.mapper;

import static uk.co.whitbread.company.employee.utils.EnumConverter.getEnum;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.company.employee.client.model.Customer;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.EmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.model.EmployeeSummary;
import uk.co.whitbread.company.employee.model.GetEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.GetEmployeesRequest;
import uk.co.whitbread.company.employee.model.GetEmployeesResponse;
import uk.co.whitbread.company.employee.model.InnBusinessEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.UserDefinedAnswer;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  @Mapping(target = "id", source = "employeeAccountId")
  @Mapping(target = "guestHistoryNumber", source = "bartGuestHistoryNumber")
  @Mapping(target = "ghNumber", source = "bartGuestHistoryNumber")
  @Mapping(target = "address.country", source = "address.countryCode")
  @Mapping(target = "employeeStatus", qualifiedByName = "toEmployeeStatus", source = "employeeStatus")
  @Mapping(target = "accessLevel", qualifiedByName = "toAccessLevel", source = "accessLevel")
  @Mapping(expression =
      "java(org.apache.commons.lang3.StringUtils.isNotEmpty(response.getCentralCardIdString()) ? "
          + "response.getCentralCardIdString() : response.getCentralCardId())", target = "centralCardId")
  Employee toEmployee(GetEmployeeResponse response);

  @Mapping(target = "companyId", source = "companyAccountId")
  @Mapping(target = "business.accessLevel", qualifiedByName = "toAccessLevelClient", source = "accessLevel")
  @Mapping(target = "business.employeeId", source = "employeeAccountId")
  @Mapping(target = "contactDetail.email", source = "emailAddress")
  Customer toCustomer(GetEmployeeResponse response);

  @Mapping(target = "success", constant = "true")
  @Mapping(target = "companyId", source = "companyAccountId")
  @Mapping(target = "employee", source = "response")
  GetEmployeeActivationResponse toGetEmployeeActivationResponse(GetEmployeeResponse response);

  InnBusinessEmployeeActivationResponse toInnBusinessEmployeeActivationResponse(
      GetEmployeeActivationResponse response);

  EmployeeAccountRequest toEmployeeAccountRequest(GetEmployeeResponse response);

  @Mapping(target = "bartGuestHistoryNumber", source = "ghNumber")
  @Mapping(target = "address.countryCode", source = "address.country")
  @Mapping(target = "centralCardIdString", source = "centralCardId")
  @Mapping(target = "centralCardId", ignore = true)
  EmployeeAccountRequest toEmployeeAccountRequest(Employee employee);

  @Mapping(target = "pageSize", source = "size")
  GetCompanyEmployeesQueryParams toGetCompanyEmployeesQueryParams(
      GetEmployeesRequest getEmployeesRequest);

  @Named("toEmployeeSummary")
  @Mapping(target = "id", source = "employeeAccountId")
  @Mapping(target = "guestHistoryNumber", source = "bartGuestHistoryNumber")
  @Mapping(target = "employeeStatus", qualifiedByName = "toEmployeeStatus", source = "employeeStatus")
  @Mapping(target = "accessLevel", qualifiedByName = "toAccessLevel", source = "accessLevel")
  @Mapping(target = "employeeId", source = "bartEmployeeId")
  EmployeeSummary toEmployeeSummary(GetEmployeeResponse getEmployeeResponse);

  @Mapping(target = "success", constant = "true")
  @Mapping(target = "pageToken", source = "continuationToken")
  @Mapping(target = "employees", source = "results", qualifiedByName = "toEmployeeSummary")
  GetEmployeesResponse toGetEmployeesResponse(
      uk.co.whitbread.shared.cdh.model.GetEmployeesResponse getEmployeesResponse);

  @Mapping(target = "companyId", source = "companyId")
  @Mapping(target = "companyName", source = "companyName")
  @Mapping(target = ".", source = "employee")
  EmployeeActivationResponse toEmployeeActivationResponse(InnBusinessEmployeeActivationResponse source);

  @Named("toEmployeeStatus")
  default EmployeeStatus toEmployeeStatus(String status) {
    return getEnum(EmployeeStatus.class, status);
  }

  @Named("toAccessLevel")
  default AccessLevel toAccessLevel(String accessLevel) {
    return getEnum(AccessLevel.class, accessLevel);
  }

  @Named("toAccessLevelClient")
  default uk.co.whitbread.company.employee.client.model.AccessLevel toAccessLevelClient(
      String accessLevel) {
    return getEnum(uk.co.whitbread.company.employee.client.model.AccessLevel.class, accessLevel);
  }

  @Mapping(target = "questionId", source = "miID")
  @Mapping(target = "answer", source = "miAnswer")
  uk.co.whitbread.shared.cdh.model.UserDefinedAnswer userDefinedAnswerToUserDefinedAnswer1(
      UserDefinedAnswer userDefinedAnswer);

  @InheritInverseConfiguration
  UserDefinedAnswer cdhUserDefinedAnswerToUserDefinedAnswer(
      uk.co.whitbread.shared.cdh.model.UserDefinedAnswer userDefinedAnswer);
}
