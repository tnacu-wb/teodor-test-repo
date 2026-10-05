package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageGroupsDto {

  private String packageGroup;
  private String packageGroupDescription;
  private List<PackageCodesDto> packageCodes;

}
