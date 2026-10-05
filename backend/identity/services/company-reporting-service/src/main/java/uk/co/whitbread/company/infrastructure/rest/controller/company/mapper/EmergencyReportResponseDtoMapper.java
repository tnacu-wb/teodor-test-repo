package uk.co.whitbread.company.infrastructure.rest.controller.company.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.company.domain.model.out.EmergencyReportResult;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.EmergencyReportResponseDto;

@Mapper(componentModel = "spring")
public interface EmergencyReportResponseDtoMapper {

  EmergencyReportResponseDto toResponseDto(EmergencyReportResult emergencyReportResults);
}
