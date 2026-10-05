package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackagesSelectionDto {

  private String id;
  @JsonProperty("price")
  private Double price;
  @JsonProperty("noOfSelections")
  private Integer noSelections;

  public PackagesSelectionDto(String id, int noOfSelections) {
    this.id = id;
    this.noSelections = noOfSelections;
  }
}
