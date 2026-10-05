package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhyDto {

  private String title;
  private String description;
  private String picture;
  private List<WhyItemDto> whyItems;
}
