package uk.co.whitbread.content.infrastructure.rest.client.footer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.footer.out.LinkItems;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkItemsAemDto;

@Mapper(componentModel = "spring")
public interface LinkItemsMapper {

  @Mapping(target = "name", source = "linkText")
  @Mapping(target = "linkSrc", source = "linkPath")
  @Mapping(target = "openInNewTab", source = "linkOpenNewTab")
  LinkItems toModel(LinkItemsAemDto linkItemsAemDto);
}
