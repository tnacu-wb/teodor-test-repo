package uk.co.whitbread.booking.domain.model.information.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelInformationResponse {

  private List<GalleryImage> galleryImages;
  private List<ThumbnailImage> thumbnailImages;
  private List<TopSectionImage> topSectionImages;
  private String brand;
  private Links links;
}
