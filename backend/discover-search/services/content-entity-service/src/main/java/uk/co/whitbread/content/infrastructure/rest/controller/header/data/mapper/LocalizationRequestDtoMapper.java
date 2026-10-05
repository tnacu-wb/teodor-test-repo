package uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationDto;

@Mapper(componentModel = "spring")
public interface LocalizationRequestDtoMapper {

  LocalizationRequest toDomainModel(LocalizationDto localizationDto);

  LocalizationDto toDtoModel(LocalizationRequest localizationRequest);
}
