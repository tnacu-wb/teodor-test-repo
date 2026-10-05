package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.domain.model.out.UserPreferenceDetails;
import uk.co.whitbread.payapp.domain.model.out.UserPreferenceSettingsItem;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserPreferencesResponseDto {

  private String tetheredUserGuid;
  private List<UserPreferenceSettingsItem> settings;
  private UserPreferenceDetails details;
}