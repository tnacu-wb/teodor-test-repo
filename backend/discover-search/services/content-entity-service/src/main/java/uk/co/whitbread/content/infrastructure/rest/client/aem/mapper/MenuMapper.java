package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Menu;

@Mapper(componentModel = "spring")
public interface MenuMapper {

  @Mapping(source = "image", target = "imageSrc")
  @Mapping(source = "path", target = "menuSrc")
  @Mapping(source = "pathLabel", target = "menuLabel")
  uk.co.whitbread.content.domain.model.hotel.out.Menu toDomainModel(Menu menu);
}
