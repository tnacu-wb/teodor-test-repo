package uk.co.whitbread.ohip.domain.model.rates.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class NegotiatedProfile {
  private List<ProfileIdList> profileIdList;
  private ProfileName profileName;
  private List<RateInfoList> rateInfoList;
  private String profileType;
}
