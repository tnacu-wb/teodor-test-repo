package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class PackagesSelectionDto {

  private String id;
  private Double price;
  private Integer noSelections;
  private List<String> scheduledList;
  private String packageGroup;
  private String ratePlanCode;

  public PackagesSelectionDto(String id, Integer noSelections) {
    this.id = id;
    this.noSelections = noSelections;
  }
}
