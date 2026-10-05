package uk.co.whitbread.reservation.domain.model.out.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaIcon {
  private String linkSrc;
  private String label;
  private boolean visible;

}
