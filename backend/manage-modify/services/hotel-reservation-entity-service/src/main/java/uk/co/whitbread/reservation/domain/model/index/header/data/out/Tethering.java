package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tethering {

  private String loginPath;
  private String feedbackUrl;
  private String analyticsUtm;
  private String worldlineLoginUrl;
  private String returnUrlPath;
  private String returnUrlText;
  private String requestedPage;
}
