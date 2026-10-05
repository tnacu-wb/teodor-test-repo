package uk.co.whitbread.content.domain.model.dlp.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Why {

  private String title;
  private String description;
  private String picture;
  private List<WhyItem> whyItems;
}
