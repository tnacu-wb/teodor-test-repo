package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayoutRequestAemDto {
  private String dictionary;
  private String language;
}
