package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.spending.domain.model.in.EmployeeSpendRequest;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;

@Mapper(componentModel = "spring")
public interface EmployeeSpendRequestDtoMapper {

  EmployeeSpendRequest toModel(EmployeeSpendRequestDto employeeSpendRequestDto);
}
