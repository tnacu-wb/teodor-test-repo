package uk.co.whitbread.content.domain.model.dlp.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Promo {
  private String picture;
  private String title;
  private String description;
  private String linkText;
  private String linkUrl;
  private String linkTarget;
  private int order;
}
