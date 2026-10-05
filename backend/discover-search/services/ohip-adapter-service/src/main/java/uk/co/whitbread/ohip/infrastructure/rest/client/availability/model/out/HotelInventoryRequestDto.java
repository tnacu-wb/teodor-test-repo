package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelInventoryRequestDto {

  private String hotelId;
  private String dateRangeStart;
  private String dateRangeEnd;

}
