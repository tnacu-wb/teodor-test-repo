package uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityCacheRequest {

  private List<String> hotelCodes;
  private String arrival;
  private String departure;
  private String language;
  private String country;
  private int rooms;
  private int[] adults;
  private int[] children;
  protected String[] type;
  private int page;
  private int size;
}
