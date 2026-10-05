package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

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
