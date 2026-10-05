package uk.co.whitbread.content.domain.model.meals.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftBundle {

  private List<PackageCode> packageCodes;
  private List<String> rate;
  private List<String> roomClass;
  private Boolean optional;
}
