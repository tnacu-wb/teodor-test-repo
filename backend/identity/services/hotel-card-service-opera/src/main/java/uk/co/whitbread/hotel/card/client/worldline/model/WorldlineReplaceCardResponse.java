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
public class WorldlineReplaceCardResponse {

  private String responseCode;
  private String data;
  private List<WorldlineErrors> errors;

}
