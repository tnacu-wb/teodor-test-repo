package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.dlp.out.FaqItem;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.FaqItemDto;

@Mapper(componentModel = "spring")
public interface FaqItemMapper {

  @Mapping(source = "acceptedAnswer", target = "answer")
  FaqItem toDomainModel(FaqItemDto faqItemDto);
}
