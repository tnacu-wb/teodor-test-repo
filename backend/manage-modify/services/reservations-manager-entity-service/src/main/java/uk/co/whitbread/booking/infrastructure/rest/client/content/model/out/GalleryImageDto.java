package uk.co.whitbread.booking.infrastructure.rest.client.content.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryImageDto {

  private String imageSrc;
  private String thumbnailSrc;
  private String alt;
  private String caption;
  private String iconSrc;
}