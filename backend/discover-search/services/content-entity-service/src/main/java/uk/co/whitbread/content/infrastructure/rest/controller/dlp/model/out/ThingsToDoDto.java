package uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThingsToDoDto {
  private String title;
  private List<ToDoItemDto> items;
}