package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.hotel.out.ThumbnailImage;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Image;

@Mapper(componentModel = "spring")
public interface ThumbnailImageMapper {

  @Mapping(source = "fileReference", target = "imageSrc")
  ThumbnailImage toDomainModel(Image image);
}
