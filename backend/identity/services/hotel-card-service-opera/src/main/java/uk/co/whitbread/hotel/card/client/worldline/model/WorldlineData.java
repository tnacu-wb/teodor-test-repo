package uk.co.whitbread.hotel.card.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WorldlineData {

  @JsonProperty("tetheredUserGUID")
  private String tetheredUserGuid;

  @JsonProperty("apiUserGUID")
  private String apiUserGuid;

}
