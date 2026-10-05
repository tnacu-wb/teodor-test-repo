package uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLinksDto {

  private String linkSrc;
  private String label;
  private String iconSrc;
}
