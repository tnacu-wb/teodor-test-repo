package uk.co.whitbread.content.domain.model.dlp.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhyItem {

  private String itemIcon;
  private String itemTitle;
  private String itemDescription;
}
