package uk.co.whitbread.content.domain.model.labels.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExtrasLabel {

  private List<Extras> extrasLabels;
  private List<String> extrasList;
}