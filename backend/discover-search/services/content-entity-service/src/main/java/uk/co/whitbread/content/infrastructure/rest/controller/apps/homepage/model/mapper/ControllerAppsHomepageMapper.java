package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.apps.homepage.in.AppsHomepageRequest;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.in.AppsHomepageRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out.AppsHomepageResponseDto;

@Mapper(componentModel = "spring")
public interface ControllerAppsHomepageMapper {

  AppsHomepageRequest toDomainModel(AppsHomepageRequestDto requestDto);

  @Mapping(target = "heading", source = "response.heading.title")
  AppsHomepageResponseDto toDto(AppsHomepageResponse response);
}