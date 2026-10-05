package uk.co.whitbread.ohip.domain.model.packages.out;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageGroups {

  private String packageGroup;
  private String packageGroupDescription;
  private List<PackageCodes> packageCodes;

}
