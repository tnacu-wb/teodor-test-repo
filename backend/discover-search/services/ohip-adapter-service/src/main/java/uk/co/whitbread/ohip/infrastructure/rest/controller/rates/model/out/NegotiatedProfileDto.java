package uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class NegotiatedProfileDto {
  private List<ProfileIdListDto> profileIdList;
  private ProfileNameDto profileName;
  private List<RateInfoListDto> rateInfoList;
  private String profileType;
}
