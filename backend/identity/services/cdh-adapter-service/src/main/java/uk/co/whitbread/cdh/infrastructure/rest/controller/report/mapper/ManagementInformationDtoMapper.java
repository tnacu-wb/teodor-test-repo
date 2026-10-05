package uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.report.in.ManagementInformation;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.ManagementInformationRequestDto;

@Mapper(componentModel = "spring")
public interface ManagementInformationDtoMapper {

  ManagementInformation toModel(ManagementInformationRequestDto managementInformationRequestDto);

}
