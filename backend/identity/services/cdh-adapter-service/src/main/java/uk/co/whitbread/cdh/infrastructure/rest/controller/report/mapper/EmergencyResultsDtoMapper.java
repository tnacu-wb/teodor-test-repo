package uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.report.out.EmergencyReportResults;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out.EmergencyReportResponseDto;

@Mapper(componentModel = "spring")
public interface EmergencyResultsDtoMapper {

  EmergencyReportResponseDto toDto(EmergencyReportResults emergencyReportResults);

}
