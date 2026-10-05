package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemInventoryRequestDto {

  private String hotelId;
  private String startDate;
  private String endDate;

}
