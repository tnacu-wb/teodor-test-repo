package uk.co.whitbread.ohip.domain.model.packages.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class PackageGroupRequest implements SelfValidation<PackageGroupRequest> {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private Set<String> packageGroupList;
  @NotEmpty
  private Set<PackageCodeRequest> packageCodeList;

}