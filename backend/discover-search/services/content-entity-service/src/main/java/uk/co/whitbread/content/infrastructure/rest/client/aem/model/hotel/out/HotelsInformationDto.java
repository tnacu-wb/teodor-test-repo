package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelsInformationDto {

  private String country;
  private String language;
  private List<String> hotelIds;
}
