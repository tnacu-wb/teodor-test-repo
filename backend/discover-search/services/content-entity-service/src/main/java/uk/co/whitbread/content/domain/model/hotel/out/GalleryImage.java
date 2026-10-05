package uk.co.whitbread.content.domain.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryImage {

  private String imageSrc;
  private String thumbnailSrc;
  private String caption;
  private String alt;
  private String iconSrc;
}