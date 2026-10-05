package uk.co.whitbread.content.infrastructure.rest.client.footer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.footer.out.SocialLinks;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.in.SocialLinksAemDto;

@Mapper(componentModel = "spring")
public interface SocialLinksMapper {

  @Mapping(source = "linkUrl", target = "linkSrc")
  @Mapping(source = "iconText", target = "label")
  @Mapping(source = "iconUrl", target = "iconSrc")
  SocialLinks toModel(SocialLinksAemDto socialLinksAemDto);
}
