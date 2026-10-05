package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payapp.domain.model.out.AppPreCheckResponse;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WorldlineAppPreCheckResponseDto;

@Mapper(componentModel = "spring")
public interface AppPreCheckResponseMapper {

  @Mapping(target = "isTetheredUser", source = "data.isTetheredUser")
  @Mapping(target = "applicationGuid", source = "data.applicationGuid")
  AppPreCheckResponse toModel(WorldlineAppPreCheckResponseDto wlAppPreCheckResponseDto);
}
