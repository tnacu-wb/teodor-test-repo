package uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GetEmployeeRequestDto;

@Mapper(componentModel = "spring")
public interface CdhEmployeeMapper {

  GetEmployeeRequestDto toGetEmployeeRequestDto(String accessContext, String accessedBy,
      String companyAccountId, String employeeAccountId);
}
