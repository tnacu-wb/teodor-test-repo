package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.GalleryImage;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TopSectionImage;

@Mapper(componentModel = "spring")
public interface HotelGalleryImageMapper {

  GalleryImage toDomainModel(TopSectionImage tsImage);
}
