package uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.report.in.EmergencyReport;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.EmergencyReportRequestDto;

@Mapper(componentModel = "spring")
public interface EmergencyReportDtoMapper {

  EmergencyReport toModel(EmergencyReportRequestDto emergencyReportRequestDto);

}
