package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistributionPayload {

  protected String[][] roomTypes;
  private List<String> hotelCodes;
  private String arrival;
  private String departure;
  private int[] adults;
  private int[] children;
  private boolean[] cot;
  private int[] roomQty;
  private String country;
  private String language;
  private int rooms;
  private Set<String> rateCodes;
  private HotelsCityTaxInfo hotelsCityTaxInfo;

  @Override
  public String toString() {
    return "DistributionPayload{"
        + "hotelCodes=" + hotelCodes
        + ", arrival='" + arrival + '\''
        + ", departure='" + departure + '\''
        + ", adults=" + Arrays.toString(adults)
        + ", children=" + Arrays.toString(children)
        + ", cot=" + Arrays.toString(cot)
        + ", roomQty=" + Arrays.toString(roomQty)
        + ", country='" + country + '\''
        + ", language='" + language + '\''
        + ", roomTypes=" + Arrays.toString(roomTypes)
        + ", rooms=" + rooms
        + ", rateCodes=" + rateCodes
        + ", hotelsCityTaxInfo=" + hotelsCityTaxInfo
        + '}';
  }
}
