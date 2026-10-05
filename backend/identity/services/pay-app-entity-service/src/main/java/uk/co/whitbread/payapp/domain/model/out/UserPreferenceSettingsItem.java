package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceSettingsItem {

  private Integer smsTypeId;
  private Boolean isSmsSelected;
  private String smsTypeDescription;
  private String smsType;
}