package uk.co.whitbread.content.infrastructure.rest.client.meals.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentsAemDto {

  private String path;
  private String label;
  private String type;
}
