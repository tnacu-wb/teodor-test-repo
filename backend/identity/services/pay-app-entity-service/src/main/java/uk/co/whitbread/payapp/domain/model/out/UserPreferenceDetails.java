package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceDetails {

  private Boolean showSmsStopsToCardholder;
  private Boolean sendCardsToCardholder;
  private String accountName;
  private Integer roleId;
  private Integer accountNumber;
  private String roleDescription;
}