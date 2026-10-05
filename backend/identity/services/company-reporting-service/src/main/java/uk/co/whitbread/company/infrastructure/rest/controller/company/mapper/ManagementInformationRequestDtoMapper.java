package uk.co.whitbread.company.infrastructure.rest.controller.company.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.company.domain.model.in.ManagementInformationRequest;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.ManagementInformationRequestDto;

@Mapper(componentModel = "spring")
public interface ManagementInformationRequestDtoMapper {

  public ManagementInformationRequest toDomainModel(String companyId, String accessContext, String accessedBy,
      ManagementInformationRequestDto managementInformationRequestDto);

}
