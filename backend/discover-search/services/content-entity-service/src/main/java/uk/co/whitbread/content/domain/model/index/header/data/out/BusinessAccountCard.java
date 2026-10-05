package uk.co.whitbread.content.domain.model.index.header.data.out;

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
