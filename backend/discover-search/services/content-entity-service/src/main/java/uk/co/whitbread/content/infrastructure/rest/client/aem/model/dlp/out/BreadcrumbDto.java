package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreadcrumbDto {

  private String title;
  private String link;
}
