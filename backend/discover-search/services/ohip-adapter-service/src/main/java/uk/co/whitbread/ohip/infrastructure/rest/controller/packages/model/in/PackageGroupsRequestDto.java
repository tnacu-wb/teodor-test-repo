package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageGroupsRequestDto {

  @NotNull
  private String hotelId;
  private Set<String> packageGroupList;
  private Set<PackageCodesRequestDto> packageCodeList;

}
