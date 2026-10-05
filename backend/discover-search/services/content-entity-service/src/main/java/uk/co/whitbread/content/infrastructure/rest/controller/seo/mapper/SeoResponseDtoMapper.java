package uk.co.whitbread.content.infrastructure.rest.controller.seo.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.seo.out.SeoResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.seo.model.out.SeoResponseDto;

@Mapper(componentModel = "spring")
public interface SeoResponseDtoMapper {

  SeoResponseDto toDto(SeoResponse seoResponse);
}
