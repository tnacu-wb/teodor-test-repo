package uk.co.whitbread.content.infrastructure.rest.client.footer.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.footer.out.LinkTabs;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkTabsAemDto;

@Mapper(componentModel = "spring", uses = {
    LinkColumnsMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LinkTabsMapper {

  @Mapping(source = "tabTitle", target = "name")
  @Mapping(source = "linkColumns", target = "columns")
  @Mapping(source = "introTitle", target = "intro.name")
  @Mapping(source = "introDescription", target = "intro.description")
  LinkTabs toModel(LinkTabsAemDto socialLinksAemDto);
}
