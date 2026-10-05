package uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageGroupsResponseOhipDto {

  @JsonProperty("packageGroupsInfo")
  private PackageGroupsInfo packageGroupsInfo;
}
