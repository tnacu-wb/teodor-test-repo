package uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageGroupsRequestOhipDto {

  private String hotelId;
  private String packageCode;

}
