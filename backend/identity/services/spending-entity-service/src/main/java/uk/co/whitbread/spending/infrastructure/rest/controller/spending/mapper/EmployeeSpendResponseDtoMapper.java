package uk.co.whitbread.spending.infrastructure.rest.controller.spending.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;

@Mapper(componentModel = "spring")
public interface EmployeeSpendResponseDtoMapper {

  List<EmployeeSpendResponseDto> toDto(List<EmployeeSpendReport> employeeSpendReports);
}
