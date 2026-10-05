package uk.co.whitbread.content.infrastructure.rest.controller.meal.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentsDto {

  private String path;
  private String label;
  private String type;
}
