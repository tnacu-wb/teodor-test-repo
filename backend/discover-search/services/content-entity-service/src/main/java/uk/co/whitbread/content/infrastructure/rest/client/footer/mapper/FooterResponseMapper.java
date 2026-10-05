package uk.co.whitbread.content.infrastructure.rest.client.footer.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.footer.out.FooterResponse;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.FooterResponseAemDto;

@Mapper(componentModel = "spring", uses = {
    SocialLinksMapper.class,
    LinkTabsMapper.class, LinkItemsMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FooterResponseMapper {

  @Mapping(source = "linkTabs", target = "tabs")
  @Mapping(source = "socialLinks", target = "socialMediaIcons")
  @Mapping(source = "copyright", target = "copyrightInfo")
  @Mapping(source = "bottomLinks", target = "bottomLinks")
  FooterResponse toModel(FooterResponseAemDto footerResponseAemDto);
}
