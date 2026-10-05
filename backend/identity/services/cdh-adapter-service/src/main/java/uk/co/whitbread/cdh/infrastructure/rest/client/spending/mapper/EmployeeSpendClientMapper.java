package uk.co.whitbread.cdh.infrastructure.rest.client.spending.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.infrastructure.rest.client.spending.model.EmployeeSpendResponse;

@Mapper(componentModel = "spring")
public interface EmployeeSpendClientMapper {

  EmployeeSpendReport toModel(EmployeeSpendResponse response);
}
