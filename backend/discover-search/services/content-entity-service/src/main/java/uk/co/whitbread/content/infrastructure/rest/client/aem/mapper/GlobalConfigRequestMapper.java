package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.in.GlobalConfigRequestDto;

@Mapper(componentModel = "spring")
public interface GlobalConfigRequestMapper {

  GlobalConfigRequestDto toDto(GlobalConfigRequest globalConfigRequest);
}
