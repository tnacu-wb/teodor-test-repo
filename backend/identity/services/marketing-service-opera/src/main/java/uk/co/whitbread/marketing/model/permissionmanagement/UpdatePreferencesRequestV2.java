package uk.co.whitbread.marketing.model.permissionmanagement;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UpdatePreferencesRequestV2 extends UpdatePreferencesRequest {

  @NotBlank
  private String contactValue;

}
