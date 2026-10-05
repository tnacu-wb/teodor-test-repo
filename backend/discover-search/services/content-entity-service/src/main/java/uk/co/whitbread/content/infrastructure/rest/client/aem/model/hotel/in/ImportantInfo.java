package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportantInfo {

  private String title;
  private List<InfoItem> infoItems;

}
