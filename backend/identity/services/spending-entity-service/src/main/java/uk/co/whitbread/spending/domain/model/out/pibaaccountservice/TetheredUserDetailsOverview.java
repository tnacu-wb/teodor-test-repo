package uk.co.whitbread.spending.domain.model.out.pibaaccountservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TetheredUserDetailsOverview {
  protected String tetheredUserGuid;
  protected String apiUserGuid;
  protected String userRole;
  protected int myCards;
}
