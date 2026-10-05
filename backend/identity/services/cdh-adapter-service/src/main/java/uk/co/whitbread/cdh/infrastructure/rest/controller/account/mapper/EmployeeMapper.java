package uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.EmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.GetEmployeeRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee.GetEmployeeResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee.GetEmployeesResponseDto;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  GetEmployeeRequest toModel(GetEmployeeRequestDto getEmployeeRequestDto);

  EmployeeSearchCriteria toModel(EmployeeSearchCriteriaDto employeeSearchCriteriaDto);

  GetEmployeeResponseDto toDto(GetEmployeeResponse employee);

  GetEmployeesResponseDto toDto(GetEmployeesResponse response);
}
