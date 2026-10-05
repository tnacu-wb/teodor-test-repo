package uk.co.whitbread.cdh.infrastructure.rest.controller.spending;

import static uk.co.whitbread.cdh.infrastructure.util.Utils.sanitizeInputString;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.cdh.domain.ports.primary.EmployeeSpendReportInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.mapper.EmployeeSpendMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/cdh")
@Tag(name = "Employee Spending Reports", description = "Endpoints for retrieving employee spending information")
public class EmployeeSpendReportController implements EmployeeSpendReportControllerApiDocumentation {

  private final EmployeeSpendReportInPort employeeSpendReportInPort;
  private final EmployeeSpendMapper employeeSpendMapper;

  @Override
  @GetMapping(value = "/companies/{companyAccountId}/employees/{employeeAccountId}/reports/employee-spend",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<EmployeeSpendResponseDto> getEmployeeSpendReport(
      @PathVariable("companyAccountId") @NotNull String companyAccountId,
      @PathVariable("employeeAccountId") @NotNull String employeeAccountId,
      @ParameterObject @Valid EmployeeSpendRequestDto employeeSpendRequestDto) {

    log.info(
        "Request to get employee spend report for companyAccountId={}, employeeAccountId={}, "
            + "fromMonthYear={}, toMonthYear={}",
        sanitizeInputString(companyAccountId),
        sanitizeInputString(employeeAccountId),
        sanitizeInputString(employeeSpendRequestDto.getFromMonthYear()),
        sanitizeInputString(employeeSpendRequestDto.getToMonthYear()));

    var request = employeeSpendMapper.toModel(companyAccountId, employeeAccountId,
        employeeSpendRequestDto);

    var reports = employeeSpendReportInPort.getEmployeeSpendReport(request);

    return employeeSpendMapper.toDto(reports);
  }
}