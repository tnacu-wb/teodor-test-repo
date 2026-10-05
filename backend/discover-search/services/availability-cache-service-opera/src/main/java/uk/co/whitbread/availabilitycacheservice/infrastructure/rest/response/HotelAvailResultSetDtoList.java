package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelAvailResultSetDtoList {

  private int total;
  private List<HotelAvailResultSetDto> operaHotelAvailabilities;
}
