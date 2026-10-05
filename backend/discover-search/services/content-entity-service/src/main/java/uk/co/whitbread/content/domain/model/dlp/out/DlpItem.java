package uk.co.whitbread.content.domain.model.dlp.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DlpItem {

  private String picture;
  private String title;
  private String link;
  private Integer order;
}
