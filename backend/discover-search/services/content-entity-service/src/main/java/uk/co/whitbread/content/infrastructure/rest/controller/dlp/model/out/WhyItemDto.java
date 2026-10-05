package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhyItemDto {

  private String itemIcon;
  private String itemTitle;
  private String itemDescription;
}
