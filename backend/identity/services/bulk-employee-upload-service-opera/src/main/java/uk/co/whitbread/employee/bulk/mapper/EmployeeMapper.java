package uk.co.whitbread.employee.bulk.mapper;

import org.mapstruct.*;
import uk.co.whitbread.employee.bulk.model.*;
import uk.co.whitbread.employee.bulk.model.companyEmployees.GetEmployeeResponse;
import uk.co.whitbread.employee.bulk.model.companyEmployees.GetEmployeesResponse;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.GetEmployeesResponseDto;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  @Mapping(target = "ghNumber", source = "bartGuestHistoryNumber")
  @Mapping(target = "address.country", source = "address.countryCode")
  @Mapping(target = "employeeStatus",
          expression = "java(uk.co.whitbread.employee.bulk.model.EmployeeStatus.valueOfIgnoreCase(response.getEmployeeStatus()))")
  @Mapping(target = "centralCardId",
      expression = "java(org.apache.commons.lang3.StringUtils.isNotEmpty(response.getCentralCardIdString()) ? "
          + "response.getCentralCardIdString() : response.getCentralCardId())")
  Employee toEmployee(GetEmployeeResponse response);

  @Mapping(target = "miID", source = "questionId")
  @Mapping(target = "miAnswer", source = "answer")
  UserDefinedAnswer toUserDefinedAnswer(uk.co.whitbread.shared.cdh.model.UserDefinedAnswer userDefinedAnswer);
  
  GetEmployeesResponse toModel(GetEmployeesResponseDto getEmployeesResponseDto);
}
