package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PackagesSelectionDto {

  private String id;
  private Integer noOfSelections;
  private List<String> scheduledList;

  public PackagesSelectionDto(String id, Integer noOfSelections) {
    this.id = id;
    this.noOfSelections = noOfSelections;
  }
}
