package uk.co.whitbread.kiosk.domain.model.checkin.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileInfo {

  private List<ProfileIdList> profileIdList;
  private ProfileDetails profile;

}
