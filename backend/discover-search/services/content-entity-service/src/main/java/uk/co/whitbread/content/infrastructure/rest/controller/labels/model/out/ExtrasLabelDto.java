package uk.co.whitbread.content.infrastructure.rest.controller.labels.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExtrasLabelDto {

  private List<ExtrasDto> extrasLabels;
  private List<String> extrasList;
}