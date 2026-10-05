package uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.ManagementInformationResponseDto;
import uk.co.whitbread.company.domain.model.out.ManagementInformationReports;

@Mapper(componentModel = "spring")
public interface CdhMiReportResponseMapper {

  ManagementInformationReports toModel(ManagementInformationResponseDto managementInformationResponseDto);

}
