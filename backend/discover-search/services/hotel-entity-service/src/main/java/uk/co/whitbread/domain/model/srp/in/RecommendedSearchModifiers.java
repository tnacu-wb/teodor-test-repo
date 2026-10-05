package uk.co.whitbread.domain.model.srp.in;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedSearchModifiers {
  private Float distanceModifier;
  private Float priceModifier;
  private Float hubModifier;
}
