package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.in;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageGroupRequestDto {

  private String hotelId;
  private Set<String> packageGroupList;

}
