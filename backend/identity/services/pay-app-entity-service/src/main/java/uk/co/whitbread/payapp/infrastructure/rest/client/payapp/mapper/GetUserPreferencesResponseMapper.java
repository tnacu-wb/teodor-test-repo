package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payapp.domain.model.out.GetUserPreferencesResponse;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.GetUserPreferencesResponseDto;

@Mapper(componentModel = "spring")
public interface GetUserPreferencesResponseMapper {

  @Mapping(target = "settings", source = "data.settings")
  @Mapping(target = "details", source = "data.details")
  GetUserPreferencesResponse toModel(GetUserPreferencesResponseDto getUserPreferencesResponseDto);

}
