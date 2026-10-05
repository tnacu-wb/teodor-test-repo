package uk.co.whitbread.hotel.card.client.worldline.model;

import java.util.List;
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
public class WorldlineCardHolderUserResponse {

  private String responseCode;
  private WorldlineData data;
  private List<WorldlineErrors> errors;

}
