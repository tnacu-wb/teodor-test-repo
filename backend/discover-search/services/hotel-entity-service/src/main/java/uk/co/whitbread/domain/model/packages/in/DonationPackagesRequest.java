package uk.co.whitbread.domain.model.packages.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationPackagesRequest {

  private String hotelId;
  private List<String> packageCodes;
}
