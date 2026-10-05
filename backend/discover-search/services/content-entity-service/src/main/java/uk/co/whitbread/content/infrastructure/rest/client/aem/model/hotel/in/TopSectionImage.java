package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSectionImage {

  private String imageSrc;
  private String thumbnailSrc;
  private String alt;
  private String caption;
  private String iconSrc;
  private List<String> tags;

}