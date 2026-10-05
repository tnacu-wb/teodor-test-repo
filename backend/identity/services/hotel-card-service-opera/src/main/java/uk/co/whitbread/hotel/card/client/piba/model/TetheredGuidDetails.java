package uk.co.whitbread.hotel.card.client.piba.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredGuidDetails {

  private String employeeId;
  private String tetheredGuid;

}
