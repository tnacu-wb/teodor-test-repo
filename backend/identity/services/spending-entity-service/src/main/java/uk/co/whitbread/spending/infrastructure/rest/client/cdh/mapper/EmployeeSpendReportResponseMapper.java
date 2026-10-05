package uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;
import uk.co.whitbread.spending.infrastructure.rest.client.cdhadapterservice.model.EmployeeSpendReportResponse;

@Mapper(componentModel = "spring")
public interface EmployeeSpendReportResponseMapper {

  List<EmployeeSpendReport> toDto(List<EmployeeSpendReportResponse> employeeSpendReportResponses);
}
