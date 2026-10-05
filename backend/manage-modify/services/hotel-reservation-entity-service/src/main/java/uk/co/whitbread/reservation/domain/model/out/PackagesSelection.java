package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
public class PackagesSelection {
  private String id;
  private Integer noOfSelections;
  private List<String> scheduledList;
  private String packageGroup;
  private String ratePlanCode;

  public PackagesSelection(String id, Integer noOfSelections) {
    this.id = id;
    this.noOfSelections = noOfSelections;
  }
}
