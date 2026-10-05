package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Content;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.HeaderContentResponseAemDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HeaderContentResponseMapper {

  @Mapping(source = "content.header", target = "header")
  @Mapping(source = "content.global", target = "global")
  @Mapping(source = "content.countries", target = "countries")
  @Mapping(source = "content.form", target = "form")
  @Mapping(source = "content.authentication", target = "authentication")
  @Mapping(source = "content.results", target = "results")
  @Mapping(source = "content.contactBanner", target = "contactBanner")
  Content toModel(HeaderContentResponseAemDto headerContentResponseAemDto);
}
