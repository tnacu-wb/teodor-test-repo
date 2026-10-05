package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEmployeesResponseDto {

  private String continuationToken;
  private Integer totalEmployeesInCompany;
  private List<GetEmployeeResponseDto> results;
}