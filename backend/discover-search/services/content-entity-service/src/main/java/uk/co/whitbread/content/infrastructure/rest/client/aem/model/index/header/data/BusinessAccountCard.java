package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAccountCard {

  private String buttonLabel;
  private String textBody;
  private String tab;
  private String title;
  private String tabMobile;
  private Banner banner;
}
