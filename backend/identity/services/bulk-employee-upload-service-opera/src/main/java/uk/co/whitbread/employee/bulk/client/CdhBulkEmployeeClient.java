package uk.co.whitbread.employee.bulk.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.GetEmployeesResponseDto;

@FeignClient(value = "${feign.cdhbulkemployee.name:cdhbulkemployee}", url = "${feign.cdhbulkemployee.url:url}")
public interface CdhBulkEmployeeClient {
  
  @GetMapping("/v1/cdh/account/employees/company/{companyAccountId}")
  GetEmployeesResponseDto getCompanyEmployees(@PathVariable String companyAccountId,
                                              @RequestParam String accessedBy,
                                              @RequestParam Integer pageSize,
                                              @RequestParam(name = "pageToken", required = false) String pageToken,
                                              @RequestParam(name = "accessContext", required = false) String accessContext,
                                              @RequestParam(name = "awaitingApproval", required = false)
                                            Boolean awaitingApproval);
}
