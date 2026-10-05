package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackagesSelection {

  private String id;
  private Double price;
  private Integer noSelections;
  private String packageGroup;

  public PackagesSelection(String id, Integer noSelections) {
    this.id = id;
    this.noSelections = noSelections;
  }

  public PackagesSelection(String id, Integer noSelections, String packageGroup) {
    this.id = id;
    this.noSelections = noSelections;
    this.packageGroup = packageGroup;
  }
}
