package uk.co.whitbread.spending.infrastructure.rest.client.cdhadapterservice;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.spending.infrastructure.rest.client.cdhadapterservice.model.EmployeeSpendReportResponse;
import uk.co.whitbread.spending.infrastructure.rest.client.config.FeignErrorDecoderConfig;

@FeignClient(
    value = "${config.service.cdh-adapter-service.name:cdhadapterserviceclient}",
    url = "${config.service.cdh-adapter-service.host}",
    configuration = FeignErrorDecoderConfig.class)
public interface EmployeeSpendReportClient {

  @GetMapping(value = "${config.service.cdh-adapter-service.employeeSpendEndpoint}",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  List<EmployeeSpendReportResponse> getEmployeeSpendReport(
      @PathVariable("companyAccountId") String companyAccountId,
      @PathVariable("employeeAccountId") String employeeAccountId,
      @RequestParam("fromMonthYear") String fromMonthYear,
      @RequestParam("toMonthYear") String toMonthYear,
      @RequestParam("accessContext") String accessContext,
      @RequestParam("accessedBy") String accessedBy);
}
