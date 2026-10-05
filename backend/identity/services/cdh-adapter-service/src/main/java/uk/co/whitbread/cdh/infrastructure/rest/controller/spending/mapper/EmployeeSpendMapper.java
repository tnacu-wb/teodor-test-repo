package uk.co.whitbread.cdh.infrastructure.rest.controller.spending.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;

@Mapper(componentModel = "spring")
public interface EmployeeSpendMapper {

  @Mapping(target = "companyAccountId", source = "companyAccountId")
  @Mapping(target = "employeeAccountId", source = "employeeAccountId")
  @Mapping(target = "fromMonthYear", source = "dto.fromMonthYear")
  @Mapping(target = "toMonthYear", source = "dto.toMonthYear")
  @Mapping(target = "accessContext", source = "dto.accessContext")
  @Mapping(target = "accessedBy", source = "dto.accessedBy")
  EmployeeSpendRequest toModel(String companyAccountId, String employeeAccountId,
                               EmployeeSpendRequestDto dto);

  EmployeeSpendResponseDto toDto(EmployeeSpendReport report);
  
  List<EmployeeSpendResponseDto> toDto(List<EmployeeSpendReport> reports);
}
