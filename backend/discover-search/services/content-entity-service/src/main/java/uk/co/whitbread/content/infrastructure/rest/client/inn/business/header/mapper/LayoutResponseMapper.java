package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Layout;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.LayoutResponseDto;

@Mapper(componentModel = "spring")
public interface LayoutResponseMapper {

  @Mapping(source = "manageAccount", target = "manageAccount")
  @Mapping(source = "menu", target = "menu")
  @Mapping(source = "help", target = "help")
  @Mapping(source = "sidebar", target = "sidebar")
  Layout toModel(LayoutResponseDto layoutResponseDto);

}
