package uk.co.whitbread.reservation.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackagesSelection {
  private String id;
  private Integer noSelections;
  private String packageGroup;
  private Double price;

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
