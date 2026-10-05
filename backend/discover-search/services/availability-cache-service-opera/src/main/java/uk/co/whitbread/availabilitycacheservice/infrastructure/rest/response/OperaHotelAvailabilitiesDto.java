package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OperaHotelAvailabilitiesDto {

  private int total;

  private List<OperaHotelDto> operaHotelAvailabilities;

}
