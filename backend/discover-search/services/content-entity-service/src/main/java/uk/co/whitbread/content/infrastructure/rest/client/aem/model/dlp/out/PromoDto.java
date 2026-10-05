package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoDto {
  private String picture;
  private String title;
  private String description;
  private String linkText;
  private String linkUrl;
  private String linkTarget;
  private int order;
}