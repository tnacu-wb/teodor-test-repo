package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageCodesRequestDto {

  private Set<String> packageCodes;
}
