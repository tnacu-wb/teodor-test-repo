package uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.EmergencyReportResponseDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.EmergencyResultsDto;

@Mapper(componentModel = "spring")
public interface CdhEmergencyReportResponseMapper {

  EmergencyResultsDto toModel(EmergencyReportResponseDto emergencyReportResponseDto);

}
