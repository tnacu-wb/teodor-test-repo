package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyPolicyDto {

  private String title;
  private String description;
  private String linkLabel;
  private String linkPath;
  private String moreInfoLabel;
  private List<InfoDto> moreInfo;

}
