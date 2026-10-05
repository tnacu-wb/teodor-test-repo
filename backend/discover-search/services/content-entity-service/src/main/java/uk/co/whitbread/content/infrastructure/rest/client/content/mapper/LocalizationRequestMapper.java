package uk.co.whitbread.content.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LocalizationRequestDto;

@Mapper(componentModel = "spring")
public interface LocalizationRequestMapper {

  LocalizationRequest toDomainModel(LocalizationRequestDto localizationRequestDto);

  LocalizationRequestDto toDtoModel(LocalizationRequest localizationRequest);
}
