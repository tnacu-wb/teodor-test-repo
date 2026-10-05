package uk.co.whitbread.content.domain.model.booking.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyPolicy {

  private String name;
  private String description;
  private String linkLabel;
  private String linkSrc;
  private String moreInfoLabel;
  private List<Info> moreInfo;

}
