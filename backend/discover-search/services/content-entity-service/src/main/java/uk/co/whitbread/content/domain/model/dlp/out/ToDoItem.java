package uk.co.whitbread.content.domain.model.dlp.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToDoItem {
  private String picture;
  private String title;
  private String link;
  private String target;
  private int order;
}
