package uk.co.whitbread.content.infrastructure.rest.client.footer.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.footer.out.LinkColumns;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.LinkColumnsAemDto;

@Mapper(componentModel = "spring", uses = {
    LinkItemsMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LinkColumnsMapper {

  @Mapping(source = "columnTitle", target = "name")
  LinkColumns toModel(LinkColumnsAemDto linkColumnsAemDto);
}
