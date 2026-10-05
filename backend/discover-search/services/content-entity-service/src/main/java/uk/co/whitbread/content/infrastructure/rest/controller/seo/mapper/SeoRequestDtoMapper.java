package uk.co.whitbread.content.infrastructure.rest.controller.seo.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.seo.in.SeoRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.in.SeoRequestDto;

@Mapper(componentModel = "spring")
public interface SeoRequestDtoMapper {

  SeoRequest toModel(SeoRequestDto seoRequestDto);
}
