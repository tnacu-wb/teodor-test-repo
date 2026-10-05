package uk.co.whitbread.hotel.card.client.worldline.model;

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
public class WorldlineErrors {

  private String code;
  private String target;
  private String message;

}
