package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.GalleryImage;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.GalleryImageDto;

@Mapper(componentModel = "spring")
public interface HotelGalleryImageDtoMapper {

  GalleryImageDto toDto(GalleryImage image);
}
