package uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.InfoDto;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyPolicyDto {

  private String name;
  private String description;
  private String linkLabel;
  private String linkSrc;
  private String moreInfoLabel;
  private List<InfoDto> moreInfo;

}
