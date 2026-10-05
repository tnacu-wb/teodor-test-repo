package uk.co.whitbread.basket.domain.model.basket.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackagesSelection {
  private String id;

  @JsonProperty("price")
  private Double price;

  @JsonProperty("noOfSelections")
  private Integer noOfSelections;
}
