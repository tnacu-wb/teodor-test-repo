package uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesRequestOhipDto {

  private String hotelId;
  private String startDate;
  private String endDate;
  private Integer adults;
  private Integer children;
  private Integer nrNights;
  private String ratePlan;
}
