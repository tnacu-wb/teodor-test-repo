package uk.co.whitbread.domain.model.packages.out;

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

  private Boolean optional;
  private List<PackageCode> packageCodes;
  private List<String> rate;
  private List<String> roomClass;
}
