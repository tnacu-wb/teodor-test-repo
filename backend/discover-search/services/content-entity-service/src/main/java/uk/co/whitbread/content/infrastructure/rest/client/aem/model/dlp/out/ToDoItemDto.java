package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToDoItemDto {
  private String picture;
  private String title;
  private String link;
  private String target;
  private int order;
}
