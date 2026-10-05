package uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.EmployeeAnswers;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.EmployeeAnswersDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.ManagementInformationResponseDto;

@Mapper(componentModel = "spring")
public interface CompanyReportsDtoMapper {

  ManagementInformationResponseDto toDto(CompanyReports companyReports);

  @Mapping(target = "purchaseOrderAnswer",
      source = "purchaseOrder")
  @Mapping(target = "customerReferenceAnswer",
      source = "customerReference")
  @Mapping(target = "companyAnswers",
      source = "companyAnswer")
  EmployeeAnswersDto toDto(EmployeeAnswers employeeAnswers);


}
