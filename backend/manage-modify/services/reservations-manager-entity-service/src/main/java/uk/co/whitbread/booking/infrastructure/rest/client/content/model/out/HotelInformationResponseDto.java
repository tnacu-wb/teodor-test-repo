package uk.co.whitbread.booking.infrastructure.rest.client.content.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelInformationResponseDto {

  private List<GalleryImageDto> galleryImages;
  private List<ThumbnailImageDto> thumbnailImages;
  private List<TopSectionImageDto> topSectionImages;
  private String brand;
  private LinksDto links;
}
