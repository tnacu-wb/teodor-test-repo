package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhyDto {

  private String title;
  private String description;
  private String picture;
  private List<WhyItemDto> whyItems;
}
