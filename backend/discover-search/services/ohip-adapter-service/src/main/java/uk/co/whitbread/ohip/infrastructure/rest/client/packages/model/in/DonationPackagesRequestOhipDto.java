package uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationPackagesRequestOhipDto {

  private String hotelId;
  private List<String> packageCodes;

}
