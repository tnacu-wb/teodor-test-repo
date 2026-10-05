package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DlpItemDto {

  private String picture;
  private String title;
  private String link;
  private Integer order;
}
