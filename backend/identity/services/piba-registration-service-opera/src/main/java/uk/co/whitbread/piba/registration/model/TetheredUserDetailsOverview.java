package uk.co.whitbread.piba.registration.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredUserDetailsOverview {
  private String tetheredUserGuid;
  private String apiUserGuid;
  private String userRole;
  private int myCards;
}
