package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
public class PackagesSelection {

  private String id;
  private Integer noSelections;
  private List<String> scheduledList;
  private String packageGroup;
  private String ratePlanCode;

  public PackagesSelection(String id, Integer noSelections) {
    this.id = id;
    this.noSelections = noSelections;
  }

}
