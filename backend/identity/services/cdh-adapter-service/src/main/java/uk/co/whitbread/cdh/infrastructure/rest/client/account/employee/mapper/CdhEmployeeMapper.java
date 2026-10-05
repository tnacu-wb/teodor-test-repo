package uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.CompanyEmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.EmployeeRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.EmployeeSearchCriteriaDto;

@Mapper(componentModel = "spring")
public interface CdhEmployeeMapper {

  EmployeeSearchCriteriaDto toDto(EmployeeSearchCriteria criteria);

  EmployeeRequestDto toDto(GetEmployeeRequest request);

  @Mapping(target = "pageSize", source = "pageSize")
  @Mapping(target = "pageToken", source = "pageToken")
  @Mapping(target = "awaitingApproval", source = "awaitingApproval")
  CompanyEmployeeSearchCriteriaDto toCompanyEmployeeDto(Integer pageSize, String pageToken, Boolean awaitingApproval);

}
