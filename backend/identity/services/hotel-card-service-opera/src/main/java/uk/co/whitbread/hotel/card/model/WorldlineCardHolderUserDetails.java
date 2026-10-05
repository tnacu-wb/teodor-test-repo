package uk.co.whitbread.hotel.card.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorldlineCardHolderUserDetails {

  private String tetheredUserGuid;
  private String apiUserGuid;


}
