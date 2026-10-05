package uk.co.whitbread.ohip.domain.model.packages.in;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class PackageCodeRequest {

  private Set<String> packageCodes;

}
