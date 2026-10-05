package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromoDto {
  private String picture;
  private String title;
  private String description;
  private String linkText;
  private String linkUrl;
  private String linkTarget;
  private int order;
}