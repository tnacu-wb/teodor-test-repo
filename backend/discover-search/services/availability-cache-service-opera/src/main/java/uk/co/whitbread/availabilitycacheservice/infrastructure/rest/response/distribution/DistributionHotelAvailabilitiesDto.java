package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DistributionHotelAvailabilitiesDto {

  private int total;

  private List<DistributionHotelDto> operaHotelAvailabilities;

}
