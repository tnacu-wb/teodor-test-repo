package uk.co.whitbread.piba.registration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TetheredGuidDetails {

  private String employeeId;
  private String tetheredGuid;
}
