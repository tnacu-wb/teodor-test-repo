package uk.co.whitbread.infrastructure.rest.client.availability.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInventoryRequestOhipDto {

  private String hotelId;
  private String startDate;
  private String endDate;
  List<String> itemCodes;

}
