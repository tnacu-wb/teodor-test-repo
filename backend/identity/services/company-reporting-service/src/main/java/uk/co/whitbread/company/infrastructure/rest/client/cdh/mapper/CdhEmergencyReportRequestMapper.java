package uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.company.domain.model.in.EmergencyReportRequest;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.model.in.CdhMiReportRequestDto;

@Mapper(componentModel = "spring")
public interface CdhEmergencyReportRequestMapper {

  @Mapping(target = "fromDate", source = "fromDate")
  @Mapping(target = "toDate", source = "toDate")
  @Mapping(target = "accessContext", source = "accessContext")
  @Mapping(target = "accessedBy", source = "accessedBy")
  CdhMiReportRequestDto toDto(EmergencyReportRequest emergencyReportRequest);

}
